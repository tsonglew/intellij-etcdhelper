package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdClientManager
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.dialog.EtcdConnectionSettingsDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree

class DeleteAction : CustomAction(
    "Delete Connection",
    "Delete Connection",
    AllIcons.General.Remove
) {
    companion object {
        @JvmStatic
        fun create(
            project: Project,
            connectionTree: Tree,
            connectionManager: ConnectionManager,
            propertyUtil: PropertyUtil
        ) = DeleteAction()
            .apply {
                action = {
                    this.thisLogger().info("delete connection: $it")
                    connectionManager.removeSelectedConnection()
                }
        }
    }
}
