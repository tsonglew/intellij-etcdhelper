package com.github.tsonglew.etcdhelper.tree

import com.github.tsonglew.etcdhelper.tree.node.EtcdConnectionTreeNode
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.ui.treeStructure.Tree
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.tree.DefaultTreeModel

class ConnectionTree(
    private val singleClickLeftButton: (MouseEvent) -> Unit,
    private val doubleClickLeftButton: (MouseEvent) -> Unit,
    private val clickRightButtonOnNode: (MouseEvent) -> Unit,
    private val clickRightButtonOnBlank: (MouseEvent) -> Unit,
) : Tree() {

    init {
        model = DefaultTreeModel(EtcdConnectionTreeNode())
        cellRenderer = ConnectionTreeCellRenderer()
        alignmentX = Component.LEFT_ALIGNMENT
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                // click priority: BUTTON3 > 2 clicks > 1 click
                when {
                    // click right button
                    e?.button == MouseEvent.BUTTON3 -> {
                        selectionPath?.path?.let { clickRightButtonOnNode(e) }
                            ?: clickRightButtonOnBlank(e)
                    }

                    selectionPath?.path == null -> return
                    e?.clickCount == 2 -> doubleClickLeftButton(e)
                    e?.clickCount == 1 -> singleClickLeftButton(e)
                }
            }
        })
    }

}
