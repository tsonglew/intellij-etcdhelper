package com.github.tsonglew.etcdhelper.window

import com.github.tsonglew.etcdhelper.action.AddAction
import com.github.tsonglew.etcdhelper.action.DeleteAction
import com.github.tsonglew.etcdhelper.action.RefreshAction
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.treenode.EtcdConnectionTreeNode
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.wm.ToolWindow
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.TreeSpeedSearch
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION

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
                    super.mouseClicked(e)
                    println("clicked connection tree")
                }
            })
        }
    }

    override fun dispose() {
        connectionManager.dispose()
    }

}
