package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.intellij.icons.AllIcons
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
                    thisLogger().info("delete connection: $it")
                    connectionManager.removeSelectedConnection()
                }
        }
    }
}
