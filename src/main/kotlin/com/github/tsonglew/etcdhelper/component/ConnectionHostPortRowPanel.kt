package com.github.tsonglew.etcdhelper.component

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBTextField
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class ConnectionHostPortRowPanel(
    addRowBtnAction: (() -> Unit)?, delRowBtnAction: (() -> Unit)?,
    private val upperPanel: JPanel?
) : JPanel(FlowLayout()) {

    private val hostInputText = JBTextField("127.0.0.1", 15)
    private val portInputText = JBTextField("80", 5)
    private val addRowBtn = JButton(AllIcons.General.Add).also {
        addRowBtnAction?.also { _ ->
            it.addActionListener {
                addRowBtnAction()
                updateUI()
                this.updateUI()
            }
        }
    }
    private val delRowBtn = JButton(AllIcons.General.Remove).also {
        delRowBtnAction?.also { _ ->
            it.addActionListener {
                delRowBtnAction()
                updateUI()
                this.updateUI()
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
}
