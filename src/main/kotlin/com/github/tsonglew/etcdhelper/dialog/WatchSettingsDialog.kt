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

package com.github.tsonglew.etcdhelper.dialog

import com.github.tsonglew.etcdhelper.api.WatchItem
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.data.KeyTreeNode
import com.github.tsonglew.etcdhelper.window.MainToolWindow
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode

class WatchSettingsDialog(
    private val mainToolWindow: MainToolWindow,
    private val project: Project,
    private val connectionManager: ConnectionManager,
    private val etcdConnectionInfo: EtcdConnectionInfo? = null,
    private val selectionNode: DefaultMutableTreeNode?
) : DialogWrapper(project) {

    private lateinit var panel: JPanel
    private val keyTextField = JBTextField("", 30)
    private val isPrefixCheckBox = JBCheckBox()
    private val noPutCheckBox = JBCheckBox()
    private val noDeleteCheckBox = JBCheckBox()
    private val prevKvCheckBox = JBCheckBox()

    init {
        super.init()
        title = "Watch Settings"
        if (selectionNode != null && selectionNode is KeyTreeNode) {
            var node = selectionNode
            var fullKeyName = node.keyName
            node = node.parent
            while (node is KeyTreeNode) {
                fullKeyName = node.keyName + node.groupSymbol + fullKeyName
                if (node.parent !is KeyTreeNode) {
                    break
                }
                node = node.parent as KeyTreeNode
            }
            keyTextField.text =
                fullKeyName + if (selectionNode.isLeaf) "" else selectionNode.groupSymbol
            isPrefixCheckBox.isSelected = !selectionNode.isLeaf
        }
    }

    override fun createCenterPanel(): JComponent {
        panel = panel {
            row("key: ") { cell(keyTextField) }
            row("isPrefix: ") { cell(isPrefixCheckBox) }
            row("noPut: ") { cell(noPutCheckBox) }
            row("noDelete: ") { cell(noDeleteCheckBox) }
            row("prevKv: ") { cell(prevKvCheckBox) }
        }
        return panel
    }

    override fun doOKAction() {
        connectionManager.getClient(etcdConnectionInfo!!)!!.startWatch(toWatchItem())
        mainToolWindow.updateConnectionInfoPanel()
        close(OK_EXIT_CODE)
    }

    private fun toWatchItem(): WatchItem {
        return WatchItem(
            keyTextField.text,
            isPrefixCheckBox.isSelected,
            noPutCheckBox.isSelected,
            noDeleteCheckBox.isSelected,
            prevKvCheckBox.isSelected,
            null
        )
    }
}
