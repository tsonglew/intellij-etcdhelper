package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.action.KeyCreateAction
import com.github.tsonglew.etcdhelper.action.KeyDeleteAction
import com.github.tsonglew.etcdhelper.action.RefreshAction
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.data.KeyTreeNode
import com.github.tsonglew.etcdhelper.view.render.KeyTreeCellRenderer
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.thisLogger
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
    private val keyValueDisplayPanel: EtcdKeyValueDisplayPanel,
    splitterContainer: JBSplitter,
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
                if ((selectionPath?.pathCount ?: 0) > 1
                    && (selectionPath?.lastPathComponent as DefaultMutableTreeNode).isLeaf
                ) {
                    doubleClickKeyAction.accept(
                        (selectionPath?.lastPathComponent as KeyTreeNode).keyValue.key.toString()
                    )
                }
                thisLogger().info("click etcd key tree display panel tree")
            }
        })
    }

    private val keyTreeScrollPane = JBScrollPane(keyTree)
    private val keyDisplayLoadingDecorator = LoadingDecorator(keyTreeScrollPane, keyValueDisplayPanel, 0)
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
        add(createPagingPanel(), BorderLayout.SOUTH)
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

    fun renderKeyTree(searchSymbol: String = "/") {
        allKeys = connectionManager
            .getClient(etcdConnectionInfo)
            ?.getByPrefix(searchSymbol, 0)
            ?.apply { sortedBy { it.key.toString() } }
            ?: listOf()
        keyDisplayLoadingDecorator.startLoading(false)
        ReadAction.nonBlocking {
            try {
                flatRootNode = DefaultMutableTreeNode(etcdConnectionInfo).apply {
                    isVisible = false
                }
                groupKeyTree(keyValueDisplayPanel.groupSymbol)
                keyDisplayPanel.updateUI()
            } finally {
                keyDisplayLoadingDecorator.stopLoading()
            }
        }.submit(ThreadPoolManager.executor)
    }

    private fun createRefreshAction() = RefreshAction().apply {
        action = { renderKeyTree(keyValueDisplayPanel.searchSymbol) }
    }

    private fun createPagingPanel() = JPanel(BorderLayout()).apply {
        add(JBLabel("Page Size: " + currentPageKeys.size).apply { border = JBUI.Borders.empty() }, BorderLayout.NORTH)
        add(JBLabel("Page $pageIndex of $pageCount"))
        // TODO: page navigation
    }


    private fun createKeyCreateAction(): KeyCreateAction = KeyCreateAction.create(
        project,
        connectionManager,
        etcdConnectionInfo,
        this,
        keyValueDisplayPanel
    )

    private fun createDeleteAction(): KeyDeleteAction = KeyDeleteAction.create(
        project,
        connectionManager,
        etcdConnectionInfo,
        this,
        keyValueDisplayPanel,
        keyTree
    )

    private fun groupKeyTree(groupSymbol: String) {
        if (flatRootNode == null) {
            return
        }
        groupRootNode(
            flatRootNode!!,
            LinkedHashMap<String, KeyValue>().apply {
                allKeys.forEach {
                    this[it.key.toString()] = it
                }
            },
            groupSymbol
        )
        treeModel = DefaultTreeModel(flatRootNode)
        keyTree.model = treeModel
        treeModel!!.reload()
    }

    private fun groupRootNode(
        node: DefaultMutableTreeNode,
        keys: LinkedHashMap<String, KeyValue>,
        groupSymbol: String
    ) {
        val keyNodeMap = hashMapOf<String, DefaultMutableTreeNode>()
        val nodeChildrenMap = hashMapOf<DefaultMutableTreeNode, LinkedHashMap<String, KeyValue>>()
        keys.forEach {
            val idx = it.key.indexOf(groupSymbol)
            if (idx < 0) {
                node.add(KeyTreeNode(it.value, it.key, node))
            } else {
                val newKey = it.key.substring(0, idx)
                if (!keyNodeMap.containsKey(newKey)) {
                    KeyTreeNode(it.value, newKey, node).also { n ->
                        keyNodeMap[newKey] = n
                        nodeChildrenMap[n] = LinkedHashMap()
                        node.add(n)
                    }
                }
                nodeChildrenMap[keyNodeMap[newKey]]!![it.key.substring(idx + 1)] = it.value
            }
        }
        nodeChildrenMap.forEach {
            groupRootNode(it.key, it.value, groupSymbol)
        }
    }
}
