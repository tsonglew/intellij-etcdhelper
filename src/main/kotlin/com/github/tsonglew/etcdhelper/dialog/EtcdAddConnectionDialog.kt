package com.github.tsonglew.etcdhelper.dialog

import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import com.google.api.Endpoint
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.DialogPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.layout.jbTextField
import kotlinx.coroutines.newFixedThreadPoolContext
import java.awt.TextField
import javax.swing.JComponent
import javax.swing.JPanel

class EtcdAddConnectionDialog(
    project: Project
) : DialogWrapper(project){

    private lateinit var panel: JPanel
    private val endpointsTextField =  JBTextField("endpoints", 20)
    private val usernameTextField=  JBTextField("username", 20)
    private val passwordTextField=  JBTextField("password", 20)

    init {
        super.init()
        title = "add connection"
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
        super.doOKAction()
    }

    fun toEtcdConfiguration() = EtcdConfiguration(
        endpointsTextField.text,
        usernameTextField.text,
        passwordTextField.text
    )
}