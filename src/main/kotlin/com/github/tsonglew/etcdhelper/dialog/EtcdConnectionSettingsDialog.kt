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

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PasswordUtil
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBPasswordField
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.treeStructure.Tree
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class EtcdConnectionSettingsDialog(
    private val project: Project,
    private val connectionTree: Tree,
    private val connectionManager: ConnectionManager,
    private val etcdConnectionInfo: EtcdConnectionInfo? = null
) : DialogWrapper(project) {

    private lateinit var panel: JPanel
    private val remarkTextField = JBTextField("", 20)
    private val endpointsTextField = JBTextField("http://localhost:2379", 20)
    private val usernameTextField = JBTextField("", 20)
    private val passwordTextField = JBPasswordField()

    init {
        super.init()
        title = "Add Connection"

        etcdConnectionInfo?.apply {
            remarkTextField.text = remark
            endpointsTextField.text = endpoints
            usernameTextField.text = username
            passwordTextField.text = PasswordUtil.retrievePassword(id!!)
            title = "Edit Connection"
        }
    }

    private val endpointGroups = mutableListOf<EndpointItem>()


    override fun createCenterPanel(): JComponent {
        panel = panel {
            row("remark: ") { cell(remarkTextField) }
            row("endpoints: ") { cell(endpointsTextField) }
            row("username: ") { cell(usernameTextField) }
            row("password: ") { cell(passwordTextField) }
            group("Endpoints: ") {
                row("Row 1:") {
                    label("http://")
                        .gap(RightGap.SMALL)
                    textField()
                        .gap(RightGap.SMALL)
                        .columns(20)
                        .resizableColumn()
                    label(":")
                        .gap(RightGap.SMALL)
                    textField()
                        .gap(RightGap.SMALL)
                        .columns(5)
                        .text("80")
                        .resizableColumn()
                    val action = object : DumbAwareAction(AllIcons.General.Add) {
                        override fun actionPerformed(e: AnActionEvent) {
                            panel.add(JLabel("12345"))
                        }
                    }
                    actionButton(action)
                }.layout(RowLayout.PARENT_GRID)
            }
        }
        return panel
    }

    override fun doOKAction() {
        val conf = toEtcdConfiguration()

        PropertyUtil(project).saveConnection(conf)
        PasswordUtil.savePassword(conf.id!!, String(passwordTextField.password))
        connectionManager.addConnectionToList(conf)

        close(OK_EXIT_CODE)
    }

    private fun toEtcdConfiguration() = EtcdConnectionInfo(
        endpointsTextField.text,
        usernameTextField.text,
        etcdConnectionInfo?.id,
        remarkTextField.text
    )

    data class EndpointItem(val host: String, val port: String)
}
