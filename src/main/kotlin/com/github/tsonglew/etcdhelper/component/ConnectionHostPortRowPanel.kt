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

package com.github.tsonglew.etcdhelper.component

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBTextField
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class ConnectionHostPortRowPanel(
    host: String,
    port: String,
    addRowBtnAction: (() -> Unit)?,
    delRowBtnAction: (() -> Unit)?,
    private val upperPanel: JPanel?
) : JPanel(FlowLayout()) {

    private val hostInputText = JBTextField(host, 15)
    private val portInputText = JBTextField(port, 5)
    private val addRowBtn = JButton(AllIcons.General.Add).also {
        it.preferredSize = Dimension(30, 30)
        addRowBtnAction?.also { _ ->
            it.addActionListener {
                addRowBtnAction()
                updateUI()
            }
        }
    }
    private val delRowBtn = JButton(AllIcons.General.Remove).also {
        it.preferredSize = Dimension(30, 30)
        delRowBtnAction?.also { _ ->
            it.addActionListener {
                delRowBtnAction()
                updateUI()
            }
        }
    }

    init {
        add(JLabel("Address: "))
        add(hostInputText)
        add(JLabel(":"))
        add(portInputText)
        add(addRowBtn)
        add(delRowBtn)
    }

    override fun updateUI() {
        upperPanel?.also {
            val thisIndex = it.components.indexOf(this)
            addRowBtn.isEnabled = thisIndex == it.components.lastIndex
            delRowBtn.isEnabled = thisIndex == it.components.lastIndex
        }
        super.updateUI()
    }

    infix fun toEndpointItem(useTls: Boolean) = "${
        if (useTls) "https://" else "http://"
    }${hostInputText.text}:${portInputText.text}"
}
