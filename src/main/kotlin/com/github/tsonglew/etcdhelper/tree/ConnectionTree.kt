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
