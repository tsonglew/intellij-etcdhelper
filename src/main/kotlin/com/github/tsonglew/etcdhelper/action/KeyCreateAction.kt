package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.dialog.KeyValueDialog
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyTreeDisplayPanel
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayPanel
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project

class KeyCreateAction : CustomAction(
    "Create Key",
    "Create Key",
    AllIcons.General.Add
) {
    companion object {
        @JvmStatic
        fun create(
            project: Project,
            connectionManager: ConnectionManager,
            etcdConnectionInfo: EtcdConnectionInfo,
            keyTreeDisplayPanel: EtcdKeyTreeDisplayPanel,
            keyValueDisplayPanel: EtcdKeyValueDisplayPanel
        ) = KeyCreateAction()
            .apply {
                action = {
                    KeyValueDialog(
                        project,
                        connectionManager,
                        etcdConnectionInfo,
                        keyTreeDisplayPanel,
                        keyValueDisplayPanel
                    ).show()
                }
            }
    }
}
