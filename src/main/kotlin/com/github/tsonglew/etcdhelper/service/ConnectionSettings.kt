package com.github.tsonglew.etcdhelper.service

import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.diagnostic.thisLogger

interface ConnectionSettings {

    fun getConnectionInfosList(): ArrayList<EtcdConnectionInfo>

    fun addConnection(connectionInfo: EtcdConnectionInfo) {
        val connectionInfos = getConnectionInfosList()
        val conn = connectionInfos.find { it.id == connectionInfo.id }
        if (conn != null) {
            conn.update(connectionInfo)
        } else {
            connectionInfos.add(connectionInfo)
        }
        thisLogger().info("connections after add: $connectionInfos")
    }

    fun removeConnection(connectionInfo: EtcdConnectionInfo) {
        val connectionInfos = getConnectionInfosList()
        connectionInfos.remove(connectionInfo)
        thisLogger().info("connections after remove: $connectionInfos")
    }
}
