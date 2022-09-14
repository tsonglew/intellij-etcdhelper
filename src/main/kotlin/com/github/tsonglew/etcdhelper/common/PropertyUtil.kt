package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.service.GlobalConnectionService
import com.intellij.openapi.project.Project

class PropertyUtil(
    private val project: Project
) {

    private val connectionService =  GlobalConnectionService.getInstance()

    fun saveConnection(etcdConnectionInfo: EtcdConnectionInfo) {
        connectionService.addConnection(etcdConnectionInfo)
    }

    fun getConnections() = connectionService.connections

    fun removeConnection(etcdConnectionInfo: EtcdConnectionInfo) {
    }
}
