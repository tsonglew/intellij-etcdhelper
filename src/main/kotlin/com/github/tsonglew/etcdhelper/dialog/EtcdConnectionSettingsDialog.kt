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
import com.github.tsonglew.etcdhelper.component.ConnectionHostPortRowPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.TitledSeparator
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import javax.swing.JCheckBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JPasswordField

class EtcdConnectionSettingsDialog(
    private val project: Project,
    private val connectionTree: Tree,
    private val connectionManager: ConnectionManager,
    private val etcdConnectionInfo: EtcdConnectionInfo? = null
) : DialogWrapper(project) {

    private lateinit var centerPanel: JPanel
    private lateinit var addBtnAction: () -> Unit
    private lateinit var delBtnAction: () -> Unit
    private var endpointPanel: JPanel? = null
    private val remarkTextField = JBTextField("", 20)
    private val usernameTextField = JBTextField("", 20)
    private val passwordTextField = JPasswordField("", 20)
    private val tlsCheckBox = JCheckBox("TLS")

    init {
        super.init()
        title = "Add Connection"

        etcdConnectionInfo?.apply {
            remarkTextField.text = remark
            usernameTextField.text = username
            passwordTextField.text = PasswordUtil.retrievePassword(id!!)
            title = "Edit Connection"

            while (endpointPanel!!.components.size > 1) {
                endpointPanel!!.remove(endpointPanel!!.components.last())
            }
            endpoints.split(",")
                .map { it.trimStart("http://") }
                .map { it.trimStart("https://") }
                .forEach { conn ->
                    conn.split(":").let { (host, port) ->
                        endpointPanel!!.add(newEndpointItem(host, port))
                    }
                }
            endpointPanel!!.components.filterIsInstance<ConnectionHostPortRowPanel>()
                .forEach { it.updateUI() }
        }
    }

    override fun createCenterPanel(): JComponent {
        if (endpointPanel == null) {
            endpointPanel = JPanel(VerticalFlowLayout()).apply {
                add(TitledSeparator("Endpoints"))
            }

        }
        addBtnAction = {
            endpointPanel!!.add(newEndpointItem())
        }
        delBtnAction = {
            if (endpointPanel!!.components.size > 2)
                endpointPanel.apply {
                    endpointPanel!!.remove(endpointPanel!!.components.last())
                    (endpointPanel!!.components.last() as ConnectionHostPortRowPanel).updateUI()
                }
        }
        endpointPanel!!.add(newEndpointItem())

        centerPanel = panel {
            row("name: ") { cell(remarkTextField) }
            row("username: ") { cell(usernameTextField) }
            row("password: ") { cell(passwordTextField) }
            row { cell(tlsCheckBox) }
            row { cell(endpointPanel!!) }
        }

        return centerPanel
    }

    private fun newEndpointItem(host: String = "127.0.0.1", port: String = "2379") =
        ConnectionHostPortRowPanel(
            host,
            port,
            addBtnAction,
            delBtnAction,
            endpointPanel,
        )

    override fun doOKAction() {
        val conf = toEtcdConfiguration()

        PropertyUtil(project).saveConnection(conf)
        PasswordUtil.savePassword(conf.id!!, String(passwordTextField.password))
        connectionManager.addConnectionToList(conf)

        close(OK_EXIT_CODE)
    }

    private fun toEtcdConfiguration(): EtcdConnectionInfo {
        val connAddr = endpointPanel!!.components
            .filterIsInstance<ConnectionHostPortRowPanel>()
            .joinToString(",") { it toEndpointItem tlsCheckBox.isSelected }
        return EtcdConnectionInfo(
            connAddr,
            usernameTextField.text,
            etcdConnectionInfo?.id,
            remarkTextField.text.ifBlank { connAddr }
        )
    }

    private fun String.trimStart(string: String): String {
        var result = this

        while (result.startsWith(string)) {
            result = result.substring(string.length)
        }

        return result
    }

}
