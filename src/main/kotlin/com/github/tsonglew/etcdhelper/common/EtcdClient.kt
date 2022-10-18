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

package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.common.StringUtils.string2Bytes
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import io.etcd.jetcd.*
import io.etcd.jetcd.options.DeleteOption
import io.etcd.jetcd.options.GetOption
import io.etcd.jetcd.options.LeaseOption
import io.etcd.jetcd.options.PutOption
import java.util.concurrent.TimeUnit

/**
 * @author tsonglew
 */
class EtcdClient(val project: Project) {
    private var client: Client? = null
    private var kvClient: KV? = null
    private var authClient: Auth? = null
    private var leaseClient: Lease? = null
    private lateinit var endpoints: Array<String>
    private lateinit var etcdConnectionInfo: EtcdConnectionInfo

    fun init(etcdConnectionInfo: EtcdConnectionInfo) {
        this.etcdConnectionInfo = etcdConnectionInfo
        init(
                etcdConnectionInfo.endpoints.split(",").toTypedArray(),
                etcdConnectionInfo.username,
                etcdConnectionInfo.password,
        )
    }

    fun init(etcdUrls: Array<String>, user: String?, password: String?) {
        endpoints = etcdUrls
        try {
            val clientBuilder = Client.builder().endpoints(*etcdUrls)
            if (user != null && password != null && user.isNotEmpty() && password.isNotEmpty()) {
                clientBuilder.user(bytesOf(user)).password(bytesOf(password))
            }
            client = clientBuilder.build()
            kvClient = client!!.kvClient
            authClient = client!!.authClient
            leaseClient = client!!.leaseClient
        } catch (e: Exception) {
            thisLogger().info("invalid connection info")
        }
    }

    fun close() {
        kvClient!!.close()
        authClient!!.close()
        leaseClient!!.close()
        client!!.close()
    }

    /**
     * 写入数据
     *
     * @param key           key
     * @param value         value
     * @param ttlSecs  ttl(secs)
     * @return 是否写入成功
     */
    fun put(key: String, value: String, ttlSecs: Int): Boolean {
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

    fun delete(key: String) = try {
        thisLogger().info("delete key: $key")
        kvClient!!.delete(bytesOf(key)).get()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun deleteByPrefix(key: String) = try {
        thisLogger().info("deleteByPrefix: $key")
        kvClient!!.delete(bytesOf(key), DeleteOption.newBuilder().isPrefix(true).build()).get()
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    fun getByPrefix(key: String, limit: Int?): List<KeyValue> {
        // TODO: use serializable & keyOnly to optimize performance
        try {
            val optionBuilder = GetOption.newBuilder()
                    .withRange(prefixEndOf(bytesOf(key)))
                    .withSortField(GetOption.SortTarget.MOD)
                    .withSortOrder(GetOption.SortOrder.ASCEND)
            if ((limit != null) && (limit > 0)) {
                optionBuilder.withLimit(limit.toLong())
            }
            return kvClient!![bytesOf(key), optionBuilder.build()][1L, TimeUnit.SECONDS].kvs
        } catch (e: Exception) {
            thisLogger().info("get by prefix error: ${e.message}")
            e.printStackTrace()
            Notifier.notifyError("Connection Failed", "Please check your connection info: $etcdConnectionInfo",
                    project)
        }
        return listOf()
    }

    fun get(key: String): List<KeyValue> {
        try {
            return kvClient!![bytesOf(key)].get().kvs
        } catch (e: Exception) {
            thisLogger().info("get key $key error: ${e.message}")
            e.printStackTrace()
            Notifier.notifyError("Connection Failed", "Please check your connection info: $etcdConnectionInfo", project)
        }
        return listOf()
    }

    fun getLeaseInfo(leaseId: Long) = leaseClient!!.timeToLive(leaseId, LeaseOption.DEFAULT).get().tTl

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

        private fun bytesOf(s: String?): ByteSequence {
            return ByteSequence.from(string2Bytes(s))
        }
    }
}
