package com.github.tsonglew.etcdhelper.dialog

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.event.ActionEvent
import java.util.function.Consumer
import javax.swing.JLabel
import javax.swing.border.EmptyBorder

class ConfirmDialog(
    val project: Project,
    dialogTitle: String,
    private val centralPanelText: String,
    private val customOkFunction: Consumer<ActionEvent>
) : DialogWrapper(project) {

    init {
        title = dialogTitle
        isResizable = false
        isAutoAdjustable = true
        myOKAction = CustomOkAction()
        super.init()
    }

    override fun createCenterPanel() = JLabel(centralPanelText).apply {
        border = EmptyBorder(10, 0, 10, 0)
    }

    private inner class CustomOkAction : DialogWrapperAction("OK") {
        init {
            putValue(DEFAULT_ACTION, true)
        }

        override fun doAction(e: ActionEvent) {
            customOkFunction.accept(e)
            close(OK_EXIT_CODE)
        }
    }

}
