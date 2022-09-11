package com.github.tsonglew.etcdhelper.common

import com.intellij.openapi.project.Project

class PropertyUtil(
    private val project: Project
) {
    fun getConnections(): List<EtcdConnectionInfo> = listOf();
}
