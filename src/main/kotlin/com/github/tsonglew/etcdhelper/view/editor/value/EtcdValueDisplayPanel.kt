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

package com.github.tsonglew.etcdhelper.view.editor.value

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.github.tsonglew.etcdhelper.listener.KeyReleasedListener
import com.intellij.icons.AllIcons
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.ui.VerticalFlowLayout
import com.intellij.ui.LanguageTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import io.etcd.jetcd.KeyValue
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class EtcdValueDisplayPanel : JPanel(BorderLayout()) {
    private lateinit var project: Project
    private lateinit var key: String
    private lateinit var etcdConnectionInfo: EtcdConnectionInfo
    private lateinit var connectionManager: ConnectionManager
    private lateinit var loadingDecorator: LoadingDecorator

    private lateinit var valueTextArea: LanguageTextField
    private lateinit var valueSizeLabel: JLabel
    private lateinit var revisionLabel: JLabel
    private lateinit var modRevisionLabel: JLabel
    private lateinit var versionLabel: JLabel
    private lateinit var leaseLabel: JLabel

    private var keyTextField: JBTextField? = null
    private var ttlTextField: JBTextField? = null

    fun init(
        project: Project,
        key: String,
        etcdConnectionInfo: EtcdConnectionInfo,
        connectionManager: ConnectionManager,
        loadingDecorator: LoadingDecorator
    ) {
        this.project = project
        this.key = key
        this.etcdConnectionInfo = etcdConnectionInfo
        this.connectionManager = connectionManager
        this.loadingDecorator = loadingDecorator

        loadingDecorator.startLoading(false)
        ReadAction.nonBlocking {
            try {
                ApplicationManager.getApplication().invokeLater(this::initWithValue)
            } finally {
                loadingDecorator.stopLoading()
            }
        }.submit(ThreadPoolManager.executor)
    }

    private val keyValue: KeyValue?
        get() = connectionManager.getClient(etcdConnectionInfo)?.get(key)?.let {
            if (it.isNotEmpty()) it[0] else null
        }

    private fun initWithValue() {
        this.removeAll()
        initValuePreviewToolbarPanel()
        initValuePreviewPanel()
    }

    private fun initValuePreviewToolbarPanel() {
        keyTextField = JBTextField(key).apply {
            preferredSize = Dimension(290, 30)
            minimumSize = Dimension(290, 30)
            toolTipText = text
            addKeyListener(object : KeyReleasedListener {
                override fun keyReleased(e: KeyEvent?) {
                    toolTipText = text
                }

            })
        }
        ttlTextField = JBTextField().apply {
            preferredSize = Dimension(100, 30)
            minimumSize = Dimension(100, 30)
            toolTipText = text
            addKeyListener(object : KeyReleasedListener {
                override fun keyReleased(e: KeyEvent?) {
                    toolTipText = text
                }
            })
        }
        // TODO: rename button
        // TODO: reload button
        // TODO: delete button
        // TODO: ttl button

        val valuePreviewToolbarPanel = JPanel(VerticalFlowLayout()).apply {
            add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(JBLabel("Key:"))
                add(keyTextField)
            })
            add(JPanel(FlowLayout(FlowLayout.LEFT)).apply {
                add(JBLabel("TTL:"))
                add(ttlTextField)
                add(JButton("Save", AllIcons.Actions.MenuSaveall).apply {
                    addActionListener {
                        isEnabled = false
                        connectionManager.getClient(etcdConnectionInfo)?.put(
                            key,
                            valueTextArea.text,
                            if (ttlTextField?.text?.isBlank() == true) 0 else ttlTextField?.text?.toInt()
                                ?: 0
                        )
                        renderLabels()
                        isEnabled = true
                    }
                })
                add(JButton("Reload", AllIcons.Actions.Refresh).apply {
                    addActionListener {
                        isEnabled = false
                        renderLabels()
                        isEnabled = true
                    }
                })
            })
        }
        add(valuePreviewToolbarPanel, BorderLayout.NORTH)
    }

    private fun initValuePreviewPanel() {
        valueTextArea = LanguageTextField(
            PlainTextLanguage.INSTANCE,
            project,
            keyValue?.value?.toString() ?: ""
        ).apply {
            autoscrolls = true
            setOneLineMode(false)
            minimumSize = Dimension(100, 100)
            addSettingsProvider { editor ->
                editor?.settings?.apply {
                    isUseSoftWraps = true
                    isLineNumbersShown = true
                    isFoldingOutlineShown = true
                    isWhitespacesShown = true
                    isLeadingWhitespaceShown = true
                    isRefrainFromScrolling = false
                    isAnimatedScrolling = true
                }
            }
        }

        // TODO: view as panel(choose value encoding)
        valueSizeLabel = JBLabel()
        revisionLabel = JBLabel()
        modRevisionLabel = JBLabel()
        leaseLabel = JBLabel()
        versionLabel = JBLabel()
        val valueLabelsPanel = JPanel(VerticalFlowLayout()).apply {
            add(valueSizeLabel)
            add(versionLabel)
            add(revisionLabel)
            add(modRevisionLabel)
            add(leaseLabel)
        }

        val valuePreviewAndFunctionPanel = JPanel(BorderLayout()).apply {
            add(valueLabelsPanel, BorderLayout.NORTH)
            add(valueTextArea, BorderLayout.CENTER)
        }
        add(valuePreviewAndFunctionPanel, BorderLayout.CENTER)

        renderLabels()
    }

    private fun renderLabels() {
        val kv = keyValue
        valueSizeLabel.text = "Value size: ${kv?.value?.bytes?.size} bytes; "
        revisionLabel.text = "Create Revision: ${kv?.createRevision}; "
        modRevisionLabel.text = "Mod revision: ${kv?.modRevision}; "
        versionLabel.text = "Version: ${kv?.version}; "
        kv?.lease?.apply {
            connectionManager.getClient(etcdConnectionInfo)?.getLeaseInfo(this).also {
                leaseLabel.text = "Lease: %x; ".format(this)
                ttlTextField?.text = "%d".format(it)
                ttlTextField?.toolTipText = "%d".format(it)
            }
        }
        valueTextArea.text = kv?.value?.toString() ?: ""
    }
}
