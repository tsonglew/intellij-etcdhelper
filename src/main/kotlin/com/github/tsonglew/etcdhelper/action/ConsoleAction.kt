package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.view.editor.console.ConsoleFileSystem
import com.github.tsonglew.etcdhelper.view.editor.console.ConsoleVirtualFile
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode

class ConsoleAction : CustomAction(
    "Console",
    "Open EtcdHelper Console",
    AllIcons.Debugger.Console
) {
    companion object {
        fun create(project: Project, connectionTree: Tree, connectionManager: ConnectionManager) =
            ConsoleAction()
                .apply {
                    action = {
                        connectionTree.selectionPath?.lastPathComponent?.let {
                            val connectionInfo = (it as DefaultMutableTreeNode).userObject as
                                    EtcdConnectionInfo
                            val file = ConsoleVirtualFile(
                                project,
                                connectionInfo.remark + " - Console",
                                connectionInfo,
                                connectionManager
                            )
                            ConsoleFileSystem.getInstance(project).openEditor(file)
                        }
                    }
                }
    }
}
