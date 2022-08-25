package com.github.tsonglew.etcdhelper.window

import com.github.tsonglew.etcdhelper.action.EtcdAddConnectionAction
import com.github.tsonglew.etcdhelper.action.EtcdRefreshConnectionAction
import com.github.tsonglew.etcdhelper.common.EtcdClientManager
import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import com.github.tsonglew.etcdhelper.tab.EtcdDataVirtualFile
import com.github.tsonglew.etcdhelper.treenode.EtcdConnectionTreeNode
import com.intellij.lang.Language
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.NewVirtualFile
import com.intellij.openapi.vfs.newvfs.impl.FakeVirtualFile
import com.intellij.openapi.vfs.newvfs.impl.StubVirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.JBUI
import org.jetbrains.rpc.LOG
import java.awt.BorderLayout
import java.awt.Color
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.event.TreeSelectionEvent
import javax.swing.event.TreeSelectionListener
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode
import javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION

class MainToolWindow(
    private val project: Project
) {
    lateinit var treeModel: DefaultTreeModel
    lateinit var treeStruct: Tree

    val rootPanel: JPanel

    init {
        rootPanel = JPanel(BorderLayout()).apply{
            add(
                createToolbarPanel(),
                BorderLayout.NORTH
            )
            add(
                OnePixelSplitter(true, 1f).apply {
                    firstComponent = createTreePanel()
                    secondComponent = createTablePanel()
                },
                BorderLayout.CENTER
            )
        }
        EtcdClientManager.mainToolWindow = this
    }

    companion object {
        const val actionGroup: String = "MainActionGroup"
    }

    private fun createToolbarPanel(): JComponent {
        val actionGroup = DefaultActionGroup(actionGroup, false).apply {
            add(EtcdAddConnectionAction())
            addSeparator()
            add(EtcdRefreshConnectionAction())
        }
        return JPanel(BorderLayout()).apply {
            add(
                ActionManager.getInstance()
                    .createActionToolbar("EtcdBrowser", actionGroup, true)
                    .also { it.targetComponent = this }
                    .component,
                BorderLayout.CENTER
            )
        }
    }

    private fun createTablePanel() : JComponent{
        return JPanel(BorderLayout()).apply {
            add(
                ActionManager.getInstance()
                    .createActionToolbar("table", DefaultActionGroup(), true)
                    .also { it.targetComponent = this }
                    .component,
                BorderLayout.CENTER
            )
        }
    }

    private fun initTreeStructure(): Tree {
        treeStruct = Tree(treeModel)
        treeStruct.isEditable = false
        treeStruct.isRootVisible = true
        treeStruct.emptyText.text = "etcd tree structure is empty"
        treeStruct.selectionModel.selectionMode = DISCONTIGUOUS_TREE_SELECTION;

        TreeSelectionListener {
                e -> println("select tree valueChanged $e")
        }.apply {
            treeStruct.selectionModel.addTreeSelectionListener(this)
        }

        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                println("double click tree")

//                val node = treeStruct.selectionPath?.lastPathComponent as EtcdConnectionTreeNode
                FileEditorManager.getInstance(project).openFile(
                    LightVirtualFile("test11", "content111"),
                    true
                )
                return true;
            }
        }.installOn(treeStruct)

        return treeStruct
    }

    private fun createTreePanel(): JComponent {
        treeModel = DefaultTreeModel(EtcdConnectionTreeNode())
        treeStruct = initTreeStructure()
        return JPanel(BorderLayout()).apply {
            add(
                JBScrollPane(treeStruct).apply{ border = JBUI.Borders.customLine(Color.BLACK) },
                BorderLayout.CENTER
            )
        }
    }

    fun addTreeNode(etcdConfiguration: EtcdConfiguration) {
        (treeModel.root as MutableTreeNode).apply {
            treeModel.insertNodeInto(
                EtcdConnectionTreeNode(etcdConfiguration),
                this,
                0
            )
        }
    }
}