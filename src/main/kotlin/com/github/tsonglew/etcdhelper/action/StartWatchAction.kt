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

package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.dialog.WatchSettingsDialog
import com.github.tsonglew.etcdhelper.window.MainToolWindow
import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode

class StartWatchAction : CustomAction(
    "Watch",
    "Watch",
    AllIcons.General.InspectionsEye
) {
    override fun ifEnabledNotify(): Boolean = false

    companion object {
        fun create(
            mainToolWindow: MainToolWindow,
            project: Project,
            connectionTree: Tree,
            connectionManager: ConnectionManager,
            etcdConnectionInfo: EtcdConnectionInfo
        ) =
            StartWatchAction()
                .apply {
                    action = {
                        WatchSettingsDialog(
                            mainToolWindow,
                            project,
                            connectionManager,
                            etcdConnectionInfo,
                            connectionTree.selectionPath?.lastPathComponent as? DefaultMutableTreeNode
                        ).show()
                    }
                }
    }
}
