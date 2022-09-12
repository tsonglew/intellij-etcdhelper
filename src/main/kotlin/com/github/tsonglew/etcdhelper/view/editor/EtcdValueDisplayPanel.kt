package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.ThreadPoolManager
import com.intellij.ide.AutoScrollToSourceOptionProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.PlainTextLanguage
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.ui.EditorSettingsProvider
import com.intellij.ui.LanguageTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.components.JBTextField
import io.etcd.jetcd.KeyValue
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.table.DefaultTableModel

class EtcdValueDisplayPanel(
    private val layoutManager: LayoutManager,
    private val project: Project,
    private val etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel,
    private val key: String,
    private val etcdConnectionInfo: EtcdConnectionInfo,
    private val connectionManager: ConnectionManager,
    private val loadingDecorator: LoadingDecorator
): JPanel(BorderLayout()) {

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
        get() = connectionManager.getClient(etcdConnectionInfo)?.get(key)?.get(0)

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
                    println("value preview toolbar key typed")
                }

                override fun keyPressed(e: KeyEvent?) {
                    println("value preview toolbar key pressed")
                }

                override fun keyReleased(e: KeyEvent?) {
                    println("value preview toolbar key released")
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
        }
        add(valuePreviewToolbarPanel, BorderLayout.NORTH)
    }

    private fun initValuePreviewPanel() {
        val fieldTextArea = JBTextArea().apply {
            lineWrap = true
            autoscrolls = true
        }
        val valueTextArea = LanguageTextField(
            PlainTextLanguage.INSTANCE,
            project,
            keyValue?.value?.toString() ?: ""
        ).apply {
            autoscrolls = true
            setOneLineMode(false)
            minimumSize = Dimension(100, 100)
            background = Color.BLACK.darker()
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
        val valueSizeLabel = JBLabel().apply {
            text = "Value size: ${keyValue?.value?.bytes?.size} bytes"
        }
        val valueFunctionPanel = JPanel(BorderLayout()).apply { add(valueSizeLabel, BorderLayout.WEST) }

        val valuePreviewAndFunctionPanel = JPanel(BorderLayout()).apply {
            add(valueFunctionPanel, BorderLayout.NORTH)
            add(valueTextArea, BorderLayout.CENTER)
        }
        add(valuePreviewAndFunctionPanel, BorderLayout.CENTER)
    }
}
