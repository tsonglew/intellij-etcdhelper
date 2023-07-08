package com.github.tsonglew.etcdhelper.component

import com.intellij.icons.AllIcons
import com.intellij.ui.components.JBTextField
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class TextedRowPanel(
    name: String,
    textLen: Int,
    addRowBtnAction: (() -> Unit)?,
    defaultText: String? = ""
) : JPanel(FlowLayout()) {

    private val nameLabel = JLabel(name)
    private val inputText = JBTextField(defaultText, textLen)
    private val addRowBtn = JButton(AllIcons.General.Add).apply {
        addRowBtnAction?.also {
            this.addActionListener {
                addRowBtnAction()
                updateUI()
            }
        }
    }

    fun activateAddBtn() {
        addRowBtn.isEnabled = true
    }

    fun deactivateAddBtn() {
        addRowBtn.isEnabled = false
    }

    init {
        add(nameLabel)
        add(inputText)
        add(addRowBtn)
    }
}
