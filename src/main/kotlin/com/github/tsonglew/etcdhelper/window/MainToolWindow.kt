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

import com.github.tsonglew.etcdhelper.action.AddAction
import com.github.tsonglew.etcdhelper.action.DeleteAction
import com.github.tsonglew.etcdhelper.action.EditAction
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFileSystem
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class MainToolWindow(
        private val project: Project,
        private val toolWindow: ToolWindow
) : Disposable {
    private val connectionTree = Tree().apply {
        model = DefaultTreeModel(DefaultMutableTreeNode())
    }
    private val connectionTreeLoadingDecorator = LoadingDecorator(JBScrollPane(connectionTree), this, 0)
    private val connectionPanel = JPanel().apply {
        layout = BorderLayout()
    }

    private val propertyUtil = PropertyUtil(project)
    private val connectionManager = ConnectionManager.getInstance(
            project,
            propertyUtil,
            connectionTree
    ).apply {
        initConnections(connectionTree)
    }
    private val connectionActionToolbar = ActionManager
            .getInstance()
            .createActionToolbar(
                    "ToolWindowToolbar",
                    DefaultActionGroup().apply {
                        add(AddAction.create(project, connectionTree, connectionManager))
                        add(DeleteAction.create(project, connectionTree, connectionManager, propertyUtil))
                        add(EditAction.create(project, connectionTree, connectionManager))
                        addSeparator()
                        // TODO: create expander
                    },
                    true
            ).apply {
                targetComponent = connectionPanel
                adjustTheSameSize(true)
            }

    val content = JPanel(BorderLayout()).apply {
        add(connectionActionToolbar.targetComponent!!, BorderLayout.NORTH)
        add(OnePixelSplitter(true, 1f).apply {
            firstComponent = JPanel(BorderLayout()).apply { add(JBScrollPane(connectionTree), BorderLayout.CENTER) }
        })
    }

    init {
        initConnectionTree()
        connectionPanel.add(connectionActionToolbar.component, BorderLayout.NORTH)
    }

    private fun openConnection() {
        thisLogger().info("path count: ${connectionTree.selectionPath?.pathCount}")
        connectionTreeLoadingDecorator.startLoading(false)
        val connectionTreeNodePath = connectionTree.selectionPath?.path?.get(1)
                ?: return
        try {
            ReadAction.nonBlocking<Any?> {

                val connectionNode = connectionTreeNodePath as DefaultMutableTreeNode
                val connectionInfo = connectionNode.userObject as EtcdConnectionInfo
                val f = connectionManager.getVirtualFile(connectionInfo)
                ApplicationManager.getApplication().invokeLater {
                    EtcdKeyValueDisplayVirtualFileSystem.getInstance(project).openEditor(f)
                }

            }.submit(ThreadPoolManager.executor)
        } finally {
            connectionTreeLoadingDecorator.stopLoading()
        }
    }

    private fun createCreateConnPopUp(e: MouseEvent) {
        val actionGroup = DefaultActionGroup().apply {
            add(AddAction.create(project, connectionTree, connectionManager))
        }
        ActionManager.getInstance().createActionPopupMenu("CreateConnPopUp", actionGroup).apply {
            component.show(connectionTree, e.x, e.y)
        }
    }

    private fun createEditConnPopUp(e: MouseEvent) {
        val actionGroup = DefaultActionGroup().apply {
            add(DeleteAction.create(project, connectionTree, connectionManager, propertyUtil))
            add(EditAction.create(project, connectionTree, connectionManager))
        }
        ActionManager.getInstance().createActionPopupMenu("EditConnPopUp", actionGroup).apply {
            component.show(connectionTree, e.x, e.y)
        }
    }

    private fun initConnectionTree() {
        connectionPanel.add(connectionTreeLoadingDecorator.component, BorderLayout.CENTER)
        connectionTree.apply {
            cellRenderer = ConnectionTreeCellRenderer()
            alignmentX = Component.LEFT_ALIGNMENT
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    // click priority: BUTTON3 > 2 click
                    when {
                        // click right button
                        e?.button == MouseEvent.BUTTON3 -> {
                            connectionTree.selectionPath?.path?.let { createEditConnPopUp(e) }
                                    ?: createCreateConnPopUp(e)
                        }

                        connectionTree.selectionPath?.path == null -> return
                        e?.clickCount == 2 -> openConnection()
                    }
                }
            })
        }
    }


    override fun dispose() {
        connectionManager.dispose()
    }

}
