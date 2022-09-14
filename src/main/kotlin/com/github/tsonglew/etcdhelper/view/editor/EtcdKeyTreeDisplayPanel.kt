package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.action.KeyCreateAction
import com.github.tsonglew.etcdhelper.action.KeyDeleteAction
import com.github.tsonglew.etcdhelper.action.RefreshAction
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.view.render.KeyTreeCellRenderer
import com.intellij.ide.CommonActionsManager
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.intellij.util.ui.JBUI
import io.etcd.jetcd.KeyValue
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.function.Consumer
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class EtcdKeyTreeDisplayPanel(
    private val project: Project,
    private val etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel,
    private val splitterContainer: JBSplitter,
    private val etcdConnectionInfo: EtcdConnectionInfo,
    private val connectionManager: ConnectionManager,
    private val doubleClickKeyAction: Consumer<String>
): JPanel() {
    private var pageIndex = 1
    private var pageSize = 100
    private var currentPageKeys: List<String> = listOf()
    private var flatRootNode: DefaultMutableTreeNode? = null
    private var treeModel: DefaultTreeModel? = null

    private var allKeys: List<KeyValue> = arrayListOf()
    private val keyTree = Tree().apply {
        cellRenderer = KeyTreeCellRenderer()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                super.mouseClicked(e)
                if (selectionPath?.pathCount!! > 1) {
                    val lastNode = selectionPath?.lastPathComponent as DefaultMutableTreeNode
                    if (lastNode.isLeaf) {
                        doubleClickKeyAction.accept(lastNode.userObject as String)
                    }
                }
                println("click etcd key tree display panel tree")
            }
        })
    }
    private val keyTreeScrollPane = JBScrollPane(keyTree)
    private val keyDisplayLoadingDecorator =  LoadingDecorator(keyTreeScrollPane, etcdKeyValueDisplayPanel, 0)
    private val actionManager = CommonActionsManager.getInstance()
    private val actions = DefaultActionGroup().apply{
        add(createRefreshAction())
        add(createKeyCreateAction())
        add(createDeleteAction())
        addSeparator()
    }
    private val actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actions, true)
    private val keyDisplayPanel = JPanel(BorderLayout()).apply {
        minimumSize = Dimension(255, 100)
        add(actionToolbar.component, BorderLayout.NORTH)
        add(keyDisplayLoadingDecorator.component, BorderLayout.CENTER)
        add(createPagingPanel(etcdKeyValueDisplayPanel), BorderLayout.SOUTH)
    }

    private val pageCount: Int
        get() {
            val result = allKeys.size / pageSize
            val mod = allKeys.size % pageSize
            return if (mod  > 0) result + 1 else result
        }

    init {
        actionToolbar.targetComponent = keyDisplayPanel
        splitterContainer.firstComponent = keyDisplayPanel
    }

    fun resetPageIndex() {
        pageIndex = 1
    }

    fun renderKeyTree(searchSymbol: String, groupSymbol: String) {
        allKeys = connectionManager
            .getClient(etcdConnectionInfo)
            ?.getByPrefix("/", 0)
            ?: listOf()
        keyDisplayLoadingDecorator.startLoading(false)
        ReadAction.nonBlocking {
            try {
                flatRootNode = DefaultMutableTreeNode(etcdConnectionInfo)
                allKeys.forEach {
                    flatRootNode!!.add(DefaultMutableTreeNode(it.key.toString()))
                }
                updateKeyTree(etcdKeyValueDisplayPanel.groupSymbol)
                keyDisplayPanel.updateUI()
            } finally {
                keyDisplayLoadingDecorator.stopLoading()
            }
        }.submit(ThreadPoolManager.executor)
    }

    private fun createRefreshAction() = RefreshAction().apply {
        action = { renderKeyTree(etcdKeyValueDisplayPanel.searchSymbol, etcdKeyValueDisplayPanel.groupSymbol) }
    }

    private fun createPagingPanel(parent: EtcdKeyValueDisplayPanel) = JPanel(BorderLayout()).apply {
        add(JBLabel("Page Size: " + currentPageKeys.size).apply { border = JBUI.Borders.empty() }, BorderLayout.NORTH)
        add(JBLabel("Page $pageIndex of $pageCount"))
        // TODO: page navigation
    }


    private fun createKeyCreateAction(): KeyCreateAction = KeyCreateAction.create(
        project,
        connectionManager,
        etcdConnectionInfo,
        this
    )

    private fun createDeleteAction(): KeyDeleteAction = KeyDeleteAction.create(
        project,
        connectionManager,
        etcdConnectionInfo,
        this,
        keyTree
    )

    private fun updateKeyTree(groupSymbol: String) {
        if (flatRootNode == null) {
            return
        }

        // TODO: group by group symbol
        treeModel = DefaultTreeModel(flatRootNode)
        keyTree.model = treeModel
        treeModel!!.reload()
    }
}
