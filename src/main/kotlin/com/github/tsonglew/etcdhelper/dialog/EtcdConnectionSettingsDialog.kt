package com.github.tsonglew.etcdhelper.dialog

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.Tree
import javax.swing.JComponent
import javax.swing.JPanel
import javax.xml.transform.OutputKeys

class EtcdConnectionSettingsDialog(
    private val project: Project,
    private val connectionTree: Tree,
    private val connectionManager: ConnectionManager
) : DialogWrapper(project){

    private lateinit var panel: JPanel
    private val endpointsTextField =  JBTextField("http://localhost:2379", 20)
    private val usernameTextField=  JBTextField("", 20)
    private val passwordTextField=  JBTextField("", 20)

    init {
        super.init()
        title = "Add Connection"
    }

    override fun createCenterPanel(): JComponent {
       panel= panel{
            row("endpoints: ") {
                cell(endpointsTextField)
            }
            row("username: ") {
                cell(usernameTextField)
            }
            row("password: ") {
                cell(passwordTextField)
            }
        }
        return panel
    }

    override fun doOKAction() {
        println("doOKAction")
        println("endpoints ${endpointsTextField.text}")
        println("username ${usernameTextField.text}")
        println("password ${passwordTextField.text}")

        // TODO: persist connection

        connectionManager.addConnectionToList(toEtcdConfiguration())
        close(OK_EXIT_CODE)
    }

    private fun toEtcdConfiguration() = EtcdConnectionInfo(
        endpointsTextField.text,
        usernameTextField.text,
        passwordTextField.text
    )
}
