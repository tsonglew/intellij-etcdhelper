package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.common.StringUtils.string2Bytes
import com.intellij.util.containers.toArray
import io.etcd.jetcd.*
import io.etcd.jetcd.auth.Permission
import io.etcd.jetcd.options.GetOption
import io.etcd.jetcd.options.PutOption
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author tsonglew
 */
class EtcdClient {
    private var client: Client? = null
    private var kvClient: KV? = null
    private var authClient: Auth? = null
    private var leaseClient: Lease? = null
    private lateinit var endpoints: Array<String>

    fun init(etcdConnectionInfo: EtcdConnectionInfo) {
        init(
            etcdConnectionInfo.endpoints.split(",").toTypedArray(),
            etcdConnectionInfo.username,
            etcdConnectionInfo.password
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
        } catch (e: Exception ) {
            println("invalid connection info")
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
     * @param timeoutMillis 写入的操作的 timeout
     * @return 是否写入成功
     */
    fun put(key: String, value: String, timeoutMillis: Int, ttlSecs: Int): Boolean {
        if (ttlSecs > 0) {
            return putWithTtl(key, value, timeoutMillis, ttlSecs)
        }
        try {
            kvClient!!.put(bytesOf(key), bytesOf(value))[timeoutMillis.toLong(), TimeUnit.MILLISECONDS]
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun putWithTtl(key: String, value: String, timeoutMillis: Int, ttlSecs: Int): Boolean {
        try {
            val leaseId = leaseClient!!.grant(ttlSecs.toLong()).get().id
            kvClient!!.put(
                bytesOf(key),
                bytesOf(value),
                PutOption.newBuilder().withLeaseId(leaseId).build()
            )[timeoutMillis.toLong(), TimeUnit.MILLISECONDS]
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun delete(key: String): Boolean {
        try {
            kvClient!!.delete(bytesOf(key)).get()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun getByPrefix(key: String, limit: Int?): List<KeyValue> {
        try {
            val optionBuilder = GetOption.newBuilder()
                .withRange(prefixEndOf(bytesOf(key)))
                .withSortField(GetOption.SortTarget.MOD)
                .withSortOrder(GetOption.SortOrder.ASCEND)
            if ((limit != null) && (limit > 0)) {
                optionBuilder.withLimit(limit.toLong())
            }
            return kvClient!![bytesOf(key), optionBuilder.build()].get().kvs
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf();
    }

    companion object {
        private val NO_PREFIX_END = byteArrayOf(0)
        private const val INIT = false
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
