package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.service.GlobalConnectionService
import com.intellij.openapi.project.Project

class PropertyUtil(
    private val project: Project
) {

    private val connectionService =  GlobalConnectionService.getInstance()

    fun getConnections() = connectionService.connections

    fun saveConnection(etcdConnectionInfo: EtcdConnectionInfo) = connectionService.addConnection(etcdConnectionInfo)

    fun removeConnection(etcdConnectionInfo: EtcdConnectionInfo) = connectionService.removeConnection(etcdConnectionInfo)
}
