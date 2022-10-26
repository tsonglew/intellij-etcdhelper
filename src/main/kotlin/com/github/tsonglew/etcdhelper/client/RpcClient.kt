package com.github.tsonglew.etcdhelper.client

import io.etcd.jetcd.KeyValue

interface RpcClient {
    fun get(key: String): List<KeyValue>
    fun getByPrefix(key: String, limit: Int?): List<KeyValue>
    fun close()
    fun delete(key: String): Boolean
    fun put(key: String, value: String, ttlSecs: Int): Boolean
    fun getLeaseInfo(leaseId: Long): Long
    fun deleteByPrefix(key: String): Boolean
}
