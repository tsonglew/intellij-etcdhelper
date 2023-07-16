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

import com.github.tsonglew.etcdhelper.action.*
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.table.AlarmListTableManager
import com.github.tsonglew.etcdhelper.table.MemberListTableManager
import com.github.tsonglew.etcdhelper.table.MemberStatusListTableManager
import com.github.tsonglew.etcdhelper.table.WatchListTableManager
import com.github.tsonglew.etcdhelper.treenode.EtcdConnectionTreeNode
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFileSystem
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class MainToolWindow(private val project: Project) : Disposable {
    private val connectionTree = Tree().apply {
        model = DefaultTreeModel(EtcdConnectionTreeNode())
        cellRenderer = ConnectionTreeCellRenderer()
        alignmentX = Component.LEFT_ALIGNMENT
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                // click priority: BUTTON3 > 2 clicks > 1 click
                when {
                    // click right button
                    e?.button == MouseEvent.BUTTON3 -> {
                        selectionPath?.path?.let { createEditConnPopUp(e) }
                            ?: createCreateConnPopUp(e)
                    }

                    selectionPath?.path == null -> return
                    e?.clickCount == 2 -> openConnection()
                    e?.clickCount == 1 -> updateConnectionInfoPanel()
                }
            }
        })
    }

    private val propertyUtil = PropertyUtil(project)
    private val connectionManager = ConnectionManager.getInstance(
        this,
        project,
        propertyUtil,
        connectionTree
    ).apply {
        initConnections(connectionTree)
    }

    private val connActionGrp = DefaultActionGroup().apply {
        add(ConnectionAddAction.create(project, connectionTree, connectionManager))
        add(ConnectionDeleteAction.create(project, connectionTree, connectionManager, propertyUtil))
        add(EditAction.create(project, connectionTree, connectionManager))
        addSeparator()
    }
    private val createConnActionGrp = DefaultActionGroup().apply {
        add(ConnectionAddAction.create(project, connectionTree, connectionManager))
    }
    private val editConnActionGrp = DefaultActionGroup().apply {
        add(ConnectionDeleteAction.create(project, connectionTree, connectionManager, propertyUtil))
        add(EditAction.create(project, connectionTree, connectionManager))
        addSeparator()
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
                it.adjustTheSameSize(true)
            }
        add(actionToolBar.component, BorderLayout.NORTH)
    }

    private val memberListTableManager = MemberListTableManager(connectionManager, null)
    private val alarmListTableManager = AlarmListTableManager(connectionManager, null)
    private val memberStatusListTableManager = MemberStatusListTableManager(connectionManager, null)
    private val watchListTableManager = WatchListTableManager(connectionManager, null)

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

    private fun createWatchPanel(watchListTableManager: WatchListTableManager): JPanel {
        val actions = DefaultActionGroup().apply {
            add(DeleteAction().apply { action = { watchListTableManager.deleteSelectedRows() } })
            add(RefreshAction().apply { action = { updateConnectionInfoPanel() } })
            addSeparator()
        }
        val actionsToolBar = ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.TOOLBAR, actions, true)
            .apply {
                this.targetComponent = watchListTableManager.table
            }
        return JPanel(BorderLayout()).apply {
            add(
                actionsToolBar.component,
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

    private fun createCreateConnPopUp(e: MouseEvent) {
        ActionManager.getInstance().createActionPopupMenu("CreateConnPopUp", createConnActionGrp)
            .apply {
                component.show(connectionTree, e.x, e.y)
            }
    }

    private fun createEditConnPopUp(e: MouseEvent) {
        ActionManager.getInstance().createActionPopupMenu("EditConnPopUp", editConnActionGrp)
            .apply {
                component.show(connectionTree, e.x, e.y)
            }
    }

    fun updateConnectionInfoPanel() {
        ReadAction.nonBlocking<Any?> {
            val connectionTreeNodePath = connectionTree.selectionPath?.path?.get(1)
                ?: return@nonBlocking
            val connectionNode = connectionTreeNodePath as DefaultMutableTreeNode
            val connectionInfo = connectionNode.userObject as EtcdConnectionInfo
            memberListTableManager.updateConnectionInfo(connectionInfo)
            alarmListTableManager.updateAlarmInfo(connectionInfo)
            memberStatusListTableManager.updateMemberStatusInfo(connectionInfo)
            watchListTableManager.updateConnectionInfo(connectionInfo)
        }.submit(ThreadPoolManager.executor)
    }

    override fun dispose() {
        connectionManager.dispose()
    }

}
