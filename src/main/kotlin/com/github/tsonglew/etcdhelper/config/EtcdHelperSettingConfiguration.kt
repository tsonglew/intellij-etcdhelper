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
import com.intellij.ui.components.JBTextField
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class EtcdHelperSettingConfiguration : Configurable {

    private val propertyUtil = PropertyUtil()

    private val defaultSearchSymbolTextField = JBTextField(propertyUtil.connectionService.defaultSearchSymbol, 100)
    private val defaultGroupSymbolTextField = JBTextField(propertyUtil.connectionService.defaultGroupSymbol, 100)
    private val defaultSearchLimitTextField = JBTextField("%d".format(propertyUtil.connectionService.defaultSearchLimit), 100)
    private val clearCacheBtn = JButton("Clear Cache")

    private val component = JPanel(VerticalFlowLayout()).apply {
        add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Default Search Symbol:"))
            add(defaultSearchSymbolTextField)
        })
        add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Default Group Symbol: "))
            add(defaultGroupSymbolTextField)
        })
        add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("Default Search Limit:  "))
            add(defaultSearchLimitTextField)
        })
        add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(clearCacheBtn)
        })
    }

    override fun createComponent() = component

    override fun isModified() = defaultSearchSymbolTextField.text != propertyUtil.connectionService.defaultSearchSymbol ||
            defaultGroupSymbolTextField.text != propertyUtil.connectionService.defaultGroupSymbol ||
            defaultSearchLimitTextField.text != "%d".format(propertyUtil.connectionService.defaultSearchLimit)

    override fun apply() {
        propertyUtil.connectionService.defaultSearchSymbol = defaultSearchSymbolTextField.text
        propertyUtil.connectionService.defaultGroupSymbol = defaultGroupSymbolTextField.text
        propertyUtil.connectionService.defaultSearchLimit = defaultSearchLimitTextField.text.toInt()
    }

    override fun getDisplayName() = "Etcd Helper"
}
