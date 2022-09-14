package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyTreeDisplayPanel
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode

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
            keyTree: Tree
        ) = KeyDeleteAction()
            .apply {
                action = {
                    connectionManager
                        .getClient(etcdConnectionInfo)
                        ?.delete(
                            (keyTree.selectionPath?.lastPathComponent as DefaultMutableTreeNode)
                                .userObject as String
                        )
                    keyTreeDisplayPanel.renderKeyTree("", "")
                }
            }
    }
}
