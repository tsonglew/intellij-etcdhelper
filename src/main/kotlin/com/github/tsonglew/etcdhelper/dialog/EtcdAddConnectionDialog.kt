package com.github.tsonglew.etcdhelper.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.DialogPanel
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JPanel

class EtcdAddConnectionDialog(
    project: Project
) : DialogWrapper(project){

    private lateinit var panel: JPanel

    init {
        super.init()
        title = "add connection"
    }

    override fun createCenterPanel(): JComponent? {
        return panel{
            row("endpoints: ") {
                textField()
            }
            row("username: ") {
                textField()
            }
            row("password: ") {
                textField()
            }
        }
    }
}