package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.util.io.AppendablePersistentMap.ValueDataAppender
import java.awt.BorderLayout
import java.awt.LayoutManager
import javax.swing.JPanel

class EtcdValueDisplayPanel(
    private val layoutManager: LayoutManager
): JPanel() {
    private var value: String? = null

    constructor(): this(BorderLayout())

    companion object {
        fun getInstance() = EtcdValueDisplayPanel()
    }

    fun init(
        project: Project,
        etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel,
        key: String,
        etcdConnectionInfo: EtcdConnectionInfo,
        connectionManager: ConnectionManager,
        loadingDecorator: LoadingDecorator
    ) {
        loadingDecorator.startLoading(false)
        ReadAction.nonBlocking {
            connectionManager
                .getClient(etcdConnectionInfo)
                ?.getByPrefix(key, 0)
                ?: return@nonBlocking
        }
    }

}
