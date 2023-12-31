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

package com.github.tsonglew.etcdhelper.config

import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import java.awt.Component.LEFT_ALIGNMENT
import java.awt.Dimension
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel

class EtcdHelperSettingConfiguration : Configurable {

    private val propertyUtil = PropertyUtil()

    private val defaultSearchSymbolTextField = JBTextField(propertyUtil.connectionService.defaultSearchSymbol, 10)
    private val defaultGroupSymbolTextField = JBTextField(propertyUtil.connectionService.defaultGroupSymbol, 10)
    private val defaultSearchLimitTextField = JBTextField("%d".format(propertyUtil.connectionService.defaultSearchLimit), 10)
    private val enableCheatsheetPopupCheckBox = JBCheckBox("Enable cheatsheet popup (if enabled, random etcdctl cheatsheet will be popped up when opening a project)", propertyUtil.connectionService.enableCheatsheetPopup)
    private val defaultEtcdQueryTimeoutTextField = JBTextField("%d".format(propertyUtil.connectionService.defaultEtcdQueryTimeout), 10)

    private val component = JPanel().apply {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)

            add(JPanel(VerticalFlowLayout()).apply {
                listOf(
                        JLabel("Default search symbol:"),
                        JLabel("Default group symbol:"),
                        JLabel("Default search limit:"),
                        JLabel("Default etcd query timeout:")
                ).forEach {
                    it.preferredSize = Dimension(100, 30)
                    add(it)
                }
                alignmentX = LEFT_ALIGNMENT
            })
            add(JPanel(VerticalFlowLayout()).apply {
                listOf(
                        defaultSearchSymbolTextField,
                        defaultGroupSymbolTextField,
                        defaultSearchLimitTextField,
                        defaultEtcdQueryTimeoutTextField
                ).forEach { add(it) }
            })
            alignmentX = LEFT_ALIGNMENT
        })
        add(enableCheatsheetPopupCheckBox)
    }

    override fun createComponent() = component

    override fun isModified() = defaultSearchSymbolTextField.text != propertyUtil.connectionService.defaultSearchSymbol ||
            defaultGroupSymbolTextField.text != propertyUtil.connectionService.defaultGroupSymbol ||
            defaultSearchLimitTextField.text != "%d".format(propertyUtil.connectionService.defaultSearchLimit) ||
            enableCheatsheetPopupCheckBox.isSelected != propertyUtil.connectionService
                .enableCheatsheetPopup || defaultEtcdQueryTimeoutTextField.text != "%d".format(propertyUtil.connectionService.defaultEtcdQueryTimeout)

    override fun apply() {
        propertyUtil.connectionService.defaultSearchSymbol = defaultSearchSymbolTextField.text
        propertyUtil.connectionService.defaultGroupSymbol = defaultGroupSymbolTextField.text
        propertyUtil.connectionService.defaultSearchLimit = defaultSearchLimitTextField.text.toInt()
        propertyUtil.connectionService.enableCheatsheetPopup = enableCheatsheetPopupCheckBox.isSelected
        propertyUtil.connectionService.defaultEtcdQueryTimeout =
            defaultEtcdQueryTimeoutTextField.text.toInt()
    }

    override fun getDisplayName() = "Etcd Helper"
}
