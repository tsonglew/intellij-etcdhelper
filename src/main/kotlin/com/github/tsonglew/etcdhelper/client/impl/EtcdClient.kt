/*
 * MIT License
 *
 * Copyright (c) 2022 Tsonglew
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.tsonglew.etcdhelper.client.impl

import com.github.tsonglew.etcdhelper.api.WatchItem
import com.github.tsonglew.etcdhelper.client.RpcClient
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.Notifier
import com.github.tsonglew.etcdhelper.common.PasswordUtil
import com.github.tsonglew.etcdhelper.common.StringUtils.string2Bytes
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import io.etcd.jetcd.*
import io.etcd.jetcd.Watch.Watcher
import io.etcd.jetcd.cluster.Member
import io.etcd.jetcd.maintenance.AlarmMember
import io.etcd.jetcd.options.*
import io.grpc.netty.GrpcSslContexts
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * @author tsonglew
 */
class EtcdClient(
    val etcdConnectionInfo: EtcdConnectionInfo,
    val project: Project? = null
) : RpcClient {
    private var client: Client? = null
    private var kvClient: KV? = null
    private var authClient: Auth? = null
    private var leaseClient: Lease? = null
    private var clusterClient: Cluster? = null
    private var maintenanceClient: Maintenance? = null
    private var watchClient: Watch? = null
    private var etcdUrls: Array<String>? = null

    private val watchItemMap: HashMap<String, WatchItem> = HashMap()

    init {
        etcdUrls = etcdConnectionInfo.endpoints.split(",").toTypedArray()
        val user = etcdConnectionInfo.username
        val password = PasswordUtil.retrievePassword(etcdConnectionInfo.id!!)

        try {
            val clientBuilder = Client.builder().endpoints(*etcdUrls!!)
            if (etcdConnectionInfo.enableAuth == true) {
                clientBuilder.user(bytesOf(user)).password(bytesOf(password))
            }
            if (etcdConnectionInfo.enableTls == true) {
                val cert = File(etcdConnectionInfo.tlsCaCert!!)
                val keyCertChainFile = File(etcdConnectionInfo.tlsClientCert!!)
                val keyFile = File(etcdConnectionInfo.tlsClientKey!!)
                val ctx = GrpcSslContexts.forClient()
                    .trustManager(cert)
                    .keyManager(keyCertChainFile, keyFile)
                    .build()
                clientBuilder.sslContext(ctx)
            }
            client = clientBuilder.build()
            kvClient = client!!.kvClient
            authClient = client!!.authClient
            leaseClient = client!!.leaseClient
            clusterClient = client!!.clusterClient
            maintenanceClient = client!!.maintenanceClient
            watchClient = client!!.watchClient
        } catch (e: Exception) {
            thisLogger().info("invalid connection info")
        }
    }

    override fun close() {
        kvClient?.close()
        authClient?.close()
        leaseClient?.close()
        clusterClient?.close()
        maintenanceClient?.close()
        watchClient?.close()

        client!!.close()
    }

    override fun put(key: String, value: String, ttlSecs: Int): Boolean {
        if (ttlSecs > 0) {
            return putWithTtl(key, value, ttlSecs)
        }
        try {
            kvClient!!.put(bytesOf(key), bytesOf(value))[1L, TimeUnit.SECONDS]
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun putWithTtl(key: String, value: String, ttlSecs: Int): Boolean {
        try {
            val leaseId = leaseClient!!.grant(ttlSecs.toLong()).get().id
            kvClient!!.put(
                bytesOf(key),
                bytesOf(value),
                PutOption.newBuilder().withLeaseId(leaseId).build()
            )[1L, TimeUnit.SECONDS]
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    override fun startWatch(watchItem: WatchItem): Watcher {
        if (watchItemMap.containsKey(watchItem.toString())) {
            return watchItemMap[watchItem.toString()]!!.watcher!!
        }
        watchItemMap[watchItem.toString()] = watchItem
        watchItem.watcher = watchClient!!.watch(
            bytesOf(watchItem.key),
            WatchOption.newBuilder()
                .isPrefix(watchItem.isPrefix)
                .withNoDelete(watchItem.noDelete)
                .withNoDelete(watchItem.noDelete)
                .withPrevKV(watchItem.prevKv)
                .build()
        ) {
            it.events.forEach { e ->
                Notifier.notifyInfo(
                    "EtcdHelper WatchResponse",
                    "type: ${e.eventType}, " +
                            "watch key: ${watchItem.key}, " +
                            "event key: ${e.keyValue.key}, " +
                            "value: ${e.keyValue.value}",
                    project
                )
            }
        }
        return watchItem.watcher!!
    }

    override fun stopWatch(watchItem: WatchItem) {
        watchItemMap[watchItem.toString()]?.watcher?.apply {
            close()
            watchItemMap.remove(watchItem.toString())
        }
    }

    override fun getWatchItems() = ArrayList(this.watchItemMap.values)
    override fun isActive(): Boolean = try {
        client?.kvClient
        true
    } catch (e: Exception) {
        false
    }

    override fun delete(key: String) = try {
        thisLogger().info("delete key: $key")
        kvClient!!.delete(bytesOf(key)).get()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    override fun deleteByPrefix(key: String) = try {
        thisLogger().info("deleteByPrefix: $key")
        kvClient!!.delete(bytesOf(key), DeleteOption.newBuilder().isPrefix(true).build()).get()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    override fun getByPrefix(key: String, limit: Int?): List<KeyValue> {
        // TODO: use serializable & keyOnly to optimize performance
        try {
            val searchKey = if (key.isBlank()) ByteSequence.from(byteArrayOf(0)) else bytesOf(key)
            val endKey =
                if (key.isBlank()) ByteSequence.from(byteArrayOf(0)) else prefixEndOf(searchKey)
            val optionBuilder = GetOption.newBuilder().withRange(endKey)
            if ((limit != null) && (limit > 0)) {
                optionBuilder.withLimit(limit.toLong())
            }
            return kvClient!![searchKey, optionBuilder.build()][1L, TimeUnit.SECONDS].kvs
        } catch (e: Exception) {
            thisLogger().info("get by prefix error: ${e.message}")
            e.printStackTrace()
            project ?: return emptyList()
            Notifier.notifyError(
                "Connection Failed", "Please check your connection info: $etcdConnectionInfo, " +
                        "error: $e",
                project
            )
        }
        return emptyList()
    }

    // User management
    fun listUsers() {
        // TODO: etcdctl user list
    }

    fun addUser() {
        // TODO: etcdctl user add myusername
    }

    fun grantRoleToUser() {
        // TODO: etcdctl user grant myusername --roles foo,bar,baz
    }

    fun revokeRole() {
        // TODO: etcdctl user revoke myusername --roles foo,bar,baz
    }

    fun getUserInfo() {
        // TODO: etcdctl user get myusername
    }

    fun changePassword() {
        // TODO: etcdctl user passwd myusername
    }

    fun removeUser() {
        // TODO: etcdctl user remove myusername
    }

    // Role management
    fun listRoles() {
        // TODO: etcdctl role list
    }

    fun addRole() {
        // TODO: etcdctl role add myrolename
    }

    fun grantPermissionToRole() {
        // TODO: etcdctl role grant-permission myrolename read /foo
        // TODO: etcdctl role grant-permission myrolename --prefix=true read /foo/; ( range [/foo/, /foo0) )
    }

    fun getRolePermission() {
        // TODO: etcdctl role get myrolename
    }

    fun revokePermission() {
        // TODO: etcdctl role revoke-permission myrolename /foo
    }

    fun removeRole() {
        // TODO: etcdctl role remove myrolename
    }

    override fun get(key: String): List<KeyValue> {
        try {
            return kvClient!![bytesOf(key)].get(1L, TimeUnit.SECONDS).kvs
        } catch (e: Exception) {
            thisLogger().info("get key $key error: ${e.message}")
            e.printStackTrace()
            project ?: return emptyList()
            Notifier.notifyError(
                "Connection Failed",
                "Please check your connection info: $etcdConnectionInfo, error: $e",
                project
            )
        }
        return emptyList()
    }

    override fun getLeaseInfo(leaseId: Long) =
        leaseClient!!.timeToLive(leaseId, LeaseOption.DEFAULT).get().tTl

    override fun listClusterMembers(): MutableList<Member> = try {
        clusterClient!!.listMember().get(1, TimeUnit.SECONDS).members
    } catch (e: Exception) {
        thisLogger().info("list cluster members error: ${e.message}")
        Notifier.notifyError(
            "Connection Failed",
            "Please check your connection info: $etcdConnectionInfo, error: $e",
            project
        )
        mutableListOf()
    }

    override fun listAlarms(): MutableList<AlarmMember> = try {
        maintenanceClient!!.listAlarms().get(1, TimeUnit.SECONDS).alarms
    } catch (e: Exception) {
        thisLogger().info("list alarms error: ${e.message}")
        Notifier.notifyError(
            "Connection Failed",
            "Please check your connection info: $etcdConnectionInfo, error: $e",
            project
        )
        mutableListOf()
    }

    override fun listMemberStatus() = try {
        etcdUrls!!.map {
            maintenanceClient!!.statusMember(it).get(1, TimeUnit.SECONDS)
        }.toMutableList()
    } catch (e: Exception) {
        thisLogger().info("list member status error: ${e.message}")
        Notifier.notifyError(
            "Connection Failed",
            "Please check your connection info: $etcdConnectionInfo, error: $e",
            project
        )
        mutableListOf()
    }

    companion object {
        private val NO_PREFIX_END = byteArrayOf(0)
        private fun prefixEndOf(prefix: ByteSequence): ByteSequence {
            val endKey = prefix.bytes.clone()
            for (i in endKey.indices.reversed()) {
                if (endKey[i].toInt() != -1) {
                    ++endKey[i]
                    return ByteSequence.from(endKey.copyOf(i + 1))
                }
            }
            return ByteSequence.from(NO_PREFIX_END)
        }

        private fun prefixEndOf(prefix: String): String {
            return prefixEndOf(bytesOf(prefix)).toString()
        }

        fun bytesOf(s: String?): ByteSequence {
            return ByteSequence.from(string2Bytes(s))
        }
    }
}
