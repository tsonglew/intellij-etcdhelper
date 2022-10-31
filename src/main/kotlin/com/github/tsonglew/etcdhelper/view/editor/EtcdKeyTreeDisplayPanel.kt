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
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
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
) : JPanel() {
    private var pageIndex = 1
    private var flatRootNode: DefaultMutableTreeNode? = null
    private var treeModel: DefaultTreeModel? = null
    private var selectedLeaf: KeyTreeNode? = null

    private var allKeys: List<KeyValue> = arrayListOf()
    private val keyTree = Tree().apply {
        cellRenderer = KeyTreeCellRenderer()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e?.clickCount == 2 && (selectionPath?.pathCount ?: 0) >= 2) {
                    val selectedNode = selectionPath?.lastPathComponent as DefaultMutableTreeNode
                    if (selectedNode.isLeaf && selectedNode != selectedLeaf) {
                        selectedLeaf = selectedNode as KeyTreeNode
                        doubleClickKeyAction.accept(selectedLeaf!!.keyValue.key.toString())
                    }
                }
            }
        })
    }

    private val keyTreeScrollPane = JBScrollPane(keyTree)
    private val keyDisplayLoadingDecorator = LoadingDecorator(keyTreeScrollPane, keyValueDisplayPanel, 0)
    private val actions = DefaultActionGroup().apply {
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
        add(createKeyCountPanel(), BorderLayout.SOUTH)
    }
    private val keyCountPanel = createKeyCountPanel()

    init {
        actionToolbar.targetComponent = keyDisplayPanel
        splitterContainer.firstComponent = keyDisplayPanel
    }

    fun resetPageIndex() {
        pageIndex = 1
    }

    fun renderKeyTree(searchSymbol: String = "/") {
        allKeys = connectionManager.getClient(etcdConnectionInfo)
                ?.getByPrefix(searchSymbol, 0)
                ?.apply { sortedBy { it.key.toString() } }
                ?: listOf()
        keyDisplayLoadingDecorator.startLoading(false)
        ReadAction.nonBlocking {
            try {
                flatRootNode = DefaultMutableTreeNode(etcdConnectionInfo).apply {
                    isVisible = false
                }
                groupKeyTree()
                keyDisplayPanel.updateUI()
                keyCountPanel.updateUI()
            } finally {
                keyDisplayLoadingDecorator.stopLoading()
            }
        }.submit(ThreadPoolManager.executor)
    }

    private fun createRefreshAction() = RefreshAction().apply {
        action = { renderKeyTree(keyValueDisplayPanel.searchSymbol) }
    }

    private fun createKeyCountPanel() = JPanel(BorderLayout()).apply {
        add(JBLabel("Key counts: ${allKeys.size}"), BorderLayout.WEST)
    }

    private fun createKeyCreateAction(): KeyCreateAction = KeyCreateAction.create(
            project,
            connectionManager,
            etcdConnectionInfo,
            this,
            keyValueDisplayPanel,
            treeModel
    )

    private fun createDeleteAction(): KeyDeleteAction = KeyDeleteAction.create(
            project,
            connectionManager,
            etcdConnectionInfo,
            this,
            keyValueDisplayPanel,
            keyTree,
            treeModel
    )

    private fun groupKeyTree() {
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
                keyValueDisplayPanel.groupSymbol
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
        if (groupSymbol.isEmpty()) {
            return
        }
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
