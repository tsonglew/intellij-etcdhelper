package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.data.KeyTreeNode
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyTreeDisplayPanel
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayPanel
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree

class KeyDeleteAction : CustomAction(
    "Delete Key",
    "Delete Key",
    AllIcons.General.Remove
) {
    companion object {
        @JvmStatic
        fun create(
            project: Project,
            connectionManager: ConnectionManager,
            etcdConnectionInfo: EtcdConnectionInfo,
            keyTreeDisplayPanel: EtcdKeyTreeDisplayPanel,
            keyValueDisplayPanel: EtcdKeyValueDisplayPanel,
            keyTree: Tree
        ) = KeyDeleteAction()
            .apply {
                action = {
                    connectionManager
                        .getClient(etcdConnectionInfo)
                        ?.delete((keyTree.selectionPath?.lastPathComponent as KeyTreeNode).keyValue.key.toString())

                    keyTreeDisplayPanel.renderKeyTree(
                        keyValueDisplayPanel.searchSymbol,
                        keyValueDisplayPanel.groupSymbol
                    )
                }
            }
    }
}
