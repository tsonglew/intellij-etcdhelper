package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdClientManager
import com.github.tsonglew.etcdhelper.dialog.EtcdConnectionSettingsDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree

class DeleteAction : CustomAction(
    "Delete Connection",
    "Delete Connection",
    AllIcons.General.Remove
) {
    companion object {
        fun create(project: Project, connectionTree: Tree, connectionManager: ConnectionManager) = DeleteAction()
            .apply {
            action = {
                println("run delete connection")
            }
        }
    }
}
