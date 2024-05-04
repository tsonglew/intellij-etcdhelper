/*
 * MIT License
 *
 * Copyright (c) 2022 Tsonglew
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.tsonglew.etcdhelper.window

import com.github.tsonglew.etcdhelper.action.DeleteAction
import com.github.tsonglew.etcdhelper.action.RefreshAction
import com.github.tsonglew.etcdhelper.action.group.ConnectionActionGroup
import com.github.tsonglew.etcdhelper.common.*
import com.github.tsonglew.etcdhelper.table.*
import com.github.tsonglew.etcdhelper.tree.ConnectionTree
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFileSystem
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.util.xml.ui.DomCollectionControl.AddAction
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.annotations.NonNls
import java.awt.BorderLayout
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.tree.DefaultMutableTreeNode

class MainToolWindow(private val project: Project) : Disposable {

    private val connectionTree: ConnectionTree = ConnectionTree(
        { _ -> updateConnectionInfoPanel() },
        { _ -> openConnection() },
        { e -> popUpAtEventPosition("EditConnPopUp", editConnActionGrp, e) },
        { e -> popUpAtEventPosition("AddConnPopUp", addConnActionGrp, e) },
    )

    private val propertyUtil = PropertyUtil(project)
    private val connectionManager = ConnectionManager.getInstance(
        this,
        project,
        propertyUtil,
        connectionTree
    ).apply {
        initConnections(connectionTree)
    }
    private val connActionGrp = createConnectionActionGroup(
        ActionTypeEnum.ADD, ActionTypeEnum.DELETE,
        ActionTypeEnum.EDIT, ActionTypeEnum.SEPARATOR
    )
    private val addConnActionGrp = createConnectionActionGroup(ActionTypeEnum.ADD)
    private val editConnActionGrp = createConnectionActionGroup(
        ActionTypeEnum.ADD,
        ActionTypeEnum.EDIT, ActionTypeEnum.SEPARATOR
    )

    private fun createConnectionActionGroup(vararg actionTypeEnum: ActionTypeEnum) =
        ConnectionActionGroup(project, connectionManager, connectionTree, *actionTypeEnum)

    private fun popUpAtEventPosition(place: @NonNls String, group: ActionGroup, e: MouseEvent) {
        ActionManager.getInstance().createActionPopupMenu(place, group)
            .apply {
                component.show(connectionTree, e.x, e.y)
            }
    }

    private val connActionPanel = JPanel().apply {
        layout = BorderLayout()
        val actionToolBar = ActionManager
            .getInstance()
            .createActionToolbar(
                "ToolWindowToolbar",
                connActionGrp,
                true
            ).also {
                it.targetComponent = this
//                it.adjustTheSameSize(true)
            }
        add(actionToolBar.component, BorderLayout.NORTH)
    }

    private val memberListTableManager = MemberListTableManager(connectionManager, null)
    private val alarmListTableManager = AlarmListTableManager(connectionManager, null)
    private val memberStatusListTableManager = MemberStatusListTableManager(connectionManager, null)
    private val watchListTableManager = WatchListTableManager(connectionManager, null)
    private val userTableManager = UserTableManager(connectionManager, null)
    private val roleTableManager = RoleTableManager(connectionManager, null)

    private val connectionInfoPanel =
        JBTabbedPane(JTabbedPane.TOP, JBTabbedPane.WRAP_TAB_LAYOUT).also {
            it.addTab(memberListTableManager.tableName, JBScrollPane(memberListTableManager.table))
            it.addTab(
                memberStatusListTableManager.tableName,
                JBScrollPane(memberStatusListTableManager.table)
            )
            it.addTab(alarmListTableManager.tableName, JBScrollPane(alarmListTableManager.table))
            it.addTab(
                watchListTableManager.tableName,
                createWatchPanel(watchListTableManager)
            )
            it.addTab(userTableManager.tableName, JBScrollPane(userTableManager.table))
            it.addTab(roleTableManager.tableName, JBScrollPane(roleTableManager.table))
        }
    val content = JPanel(BorderLayout()).apply {
        add(connActionPanel, BorderLayout.NORTH)
        add(OnePixelSplitter(true, 0.5f).apply {
            firstComponent = JPanel(BorderLayout()).apply {
                add(
                    JBScrollPane(connectionTree),
                    BorderLayout.CENTER
                )
            }
            secondComponent =
                JPanel(BorderLayout()).apply { add(connectionInfoPanel, BorderLayout.CENTER) }
        })
    }

    private fun createMemberListPanel(memberListTableManager: MemberListTableManager): JPanel {
        val actions = DefaultActionGroup().apply {
            add(AddAction().apply { })
            add(DeleteAction().apply { action = { } })
            add(RefreshAction().apply { action = { updateConnectionInfoPanel() } })
            addSeparator()
        }
        val actionToolBar = ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.TOOLBAR, actions, true)
            .apply { this.targetComponent = memberListTableManager.table }
        return JPanel(BorderLayout()).apply {
            add(actionToolBar.component, BorderLayout.NORTH)
            add(JBScrollPane(memberListTableManager.table), BorderLayout.CENTER)
        }
    }

    private fun createWatchPanel(watchListTableManager: WatchListTableManager): JPanel {
        val actions = DefaultActionGroup().apply {
            add(DeleteAction().apply { action = { watchListTableManager.deleteSelectedRows() } })
            add(RefreshAction().apply { action = { updateConnectionInfoPanel() } })
            addSeparator()
        }
        val actionToolBar = ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.TOOLBAR, actions, true)
            .apply {
                this.targetComponent = watchListTableManager.table
            }
        return JPanel(BorderLayout()).apply {
            add(
                actionToolBar.component,
                BorderLayout.NORTH
            )
            add(JBScrollPane(watchListTableManager.table), BorderLayout.CENTER)
        }
    }

    private fun openConnection() {
        val connectionTreeNodePath = connectionTree.selectionPath?.path?.get(1) ?: return
        ReadAction.nonBlocking<Any?> {

            val connectionNode = connectionTreeNodePath as DefaultMutableTreeNode
            val connectionInfo = connectionNode.userObject as EtcdConnectionInfo
            val f = connectionManager.getVirtualFile(connectionInfo)
            ApplicationManager.getApplication().invokeLater {
                EtcdKeyValueDisplayVirtualFileSystem.getInstance(project).openEditor(f)
            }

        }.submit(ThreadPoolManager.executor)
    }

    fun updateConnectionInfoPanel() {
        ReadAction.nonBlocking<Any?> {
            val connectionTreeNodePath = connectionTree.selectionPath?.path?.get(1)
                ?: return@nonBlocking
            val connectionNode = connectionTreeNodePath as DefaultMutableTreeNode
            val connectionInfo = connectionNode.userObject as EtcdConnectionInfo
            runBlocking {
                launch {
                    memberListTableManager.updateConnectionInfo(connectionInfo)
                }
                launch {
                    alarmListTableManager.updateAlarmInfo(connectionInfo)
                }
                launch {
                    memberStatusListTableManager.updateMemberStatusInfo(connectionInfo)
                }
                launch {
                    watchListTableManager.updateConnectionInfo(connectionInfo)
                }
                launch {
                    userTableManager.updateConnectionInfo(connectionInfo)
                }
                launch {
                    roleTableManager.updateConnectionInfo(connectionInfo)
                }
            }
        }.submit(ThreadPoolManager.executor)
    }

    override fun dispose() {
        connectionManager.dispose()
    }

}
