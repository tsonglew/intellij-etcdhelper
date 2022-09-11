package com.github.tsonglew.etcdhelper.window

import com.github.tsonglew.etcdhelper.action.AddAction
import com.github.tsonglew.etcdhelper.action.DeleteAction
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFile
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFileSystem
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.TreeSpeedSearch
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class MainToolWindow(
    private val project: Project,
    private val toolWindow: ToolWindow
): Disposable{
    private val connectionTree = Tree()
    private val connectionTreeLoadingDecorator = LoadingDecorator(JBScrollPane(connectionTree), this, 0)
    private val connectionPanel = JPanel().apply {
        layout=BorderLayout()
    }

    private val propertyUtil = PropertyUtil(project)
    private val connectionManager = ConnectionManager.getInstance(project, propertyUtil).apply {
        initConnections(connectionTree)
    }
    private val connectionActionToolbar = ActionManager
        .getInstance()
        .createActionToolbar(
            "ToolWindowToolbar",
            DefaultActionGroup().apply {
                add(AddAction.create(project, connectionTree, connectionManager))
                add(DeleteAction.create(project, connectionTree, connectionManager))
                addSeparator()
                // TODO: create expander
            },
            true
        ).apply {
            targetComponent = connectionPanel
            adjustTheSameSize(true)
        }
    private val connectionTreeSpeedSearch = TreeSpeedSearch(connectionTree) {
        (it.lastPathComponent as DefaultMutableTreeNode).userObject.toString()
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

    private fun initConnectionTree() {
        connectionPanel.add(connectionTreeLoadingDecorator.component, BorderLayout.CENTER)
        connectionTree.apply {
            cellRenderer = ConnectionTreeCellRenderer()
            alignmentX =Component.LEFT_ALIGNMENT
            addMouseListener(object: MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e?.clickCount == 2 && !e.isMetaDown) {
                        connectionTreeLoadingDecorator.startLoading(false)
                        try {
                            ReadAction.nonBlocking<Any?> {

                                val connectionNode = connectionTree.selectionPath?.path?.get(1) as DefaultMutableTreeNode
                                val connectionInfo = connectionNode.userObject as EtcdConnectionInfo
                                val f = EtcdKeyValueDisplayVirtualFile(
                                    project,
                                    connectionInfo.endpoints,
                                    connectionInfo,
                                    connectionManager
                                )
                                ApplicationManager.getApplication().invokeLater {
                                    EtcdKeyValueDisplayVirtualFileSystem.getInstance(project).openEditor(f)
                                    // TODO: addEditorToMap
                                }

                            }.submit(ThreadPoolManager.executor)
                        } finally {
                            connectionTreeLoadingDecorator.stopLoading()
                        }
                    }
                    println("clicked connection tree")
                }
            })
        }
    }

    override fun dispose() {
        connectionManager.dispose()
    }

}
