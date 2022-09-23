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
