package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.LanguageTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextField
import io.etcd.jetcd.KeyValue
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.LayoutManager
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class EtcdValueDisplayPanel(
    private val layoutManager: LayoutManager,
    private val project: Project,
    private val etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel,
    private val key: String,
    private val etcdConnectionInfo: EtcdConnectionInfo,
    private val connectionManager: ConnectionManager,
    private val loadingDecorator: LoadingDecorator
): JPanel(BorderLayout()) {

    private lateinit var valueTextArea: LanguageTextField
    private lateinit var valueSizeLabel: JLabel

    init {
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
        val keyTextField = JBTextField(key).apply {
            preferredSize = Dimension(200, 28)
            toolTipText = "tool tip: $text"
            addKeyListener(object : KeyListener {
                override fun keyTyped(e: KeyEvent?) {
                    thisLogger().info("value preview toolbar key typed")
                }

                override fun keyPressed(e: KeyEvent?) {
                    thisLogger().info("value preview toolbar key pressed")
                }

                override fun keyReleased(e: KeyEvent?) {
                    thisLogger().info("value preview toolbar key released")
                    toolTipText = text
                }

            })
        }
        // TODO: rename button
        // TODO: reload button
        // TODO: delete button
        // TODO: ttl button

        val valuePreviewToolbarPanel = JPanel(FlowLayout(FlowLayout.LEFT)).apply {
            add(JLabel("key: "))
            add(keyTextField)
            add(JButton("Save").apply {
                addActionListener {
                    isEnabled = false
                    val value = valueTextArea.text
                    thisLogger().info("save key $key value $value")
                    connectionManager.getClient(etcdConnectionInfo)?.put(key, value, 0)
                    isEnabled = true
                    renderLabels()
                }
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
        val valueFunctionPanel = JPanel(BorderLayout()).apply { add(valueSizeLabel, BorderLayout.WEST) }

        val valuePreviewAndFunctionPanel = JPanel(BorderLayout()).apply {
            add(valueFunctionPanel, BorderLayout.NORTH)
            add(valueTextArea, BorderLayout.CENTER)
        }
        add(valuePreviewAndFunctionPanel, BorderLayout.CENTER)

        renderLabels()
    }

    private fun renderLabels() {
        valueSizeLabel.text = "Value size: ${keyValue?.value?.bytes?.size} bytes"
    }
}
