package com.github.tsonglew.etcdhelper.action.group

import com.github.tsonglew.etcdhelper.action.ConnectionAddAction
import com.github.tsonglew.etcdhelper.action.ConnectionDeleteAction
import com.github.tsonglew.etcdhelper.action.EditAction
import com.github.tsonglew.etcdhelper.common.ActionTypeEnum
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.tree.ConnectionTree
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project

class ConnectionActionGroup(
    project: Project,
    connectionManager: ConnectionManager,
    connectionTree: ConnectionTree,
    vararg actionType: ActionTypeEnum
) : DefaultActionGroup() {
    init {
        actionType.forEach {
            when (it) {
                ActionTypeEnum.ADD -> add(
                    ConnectionAddAction.create(
                        project,
                        connectionManager
                    )
                )

                ActionTypeEnum.DELETE -> add(
                    ConnectionDeleteAction.create(
                        project,
                        connectionTree,
                        connectionManager
                    )
                )

                ActionTypeEnum.EDIT -> add(
                    EditAction.create(
                        project,
                        connectionTree,
                        connectionManager
                    )
                )

                ActionTypeEnum.SEPARATOR -> addSeparator()
            }
        }
    }
}
