package com.github.tsonglew.etcdhelper.dialog

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyTreeDisplayPanel
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JPanel

class KeyValueDialog(
    project: Project,
    private val connectionManager: ConnectionManager,
    private val etcdConnectionInfo: EtcdConnectionInfo,
    private val keyTreeDisplayPanel: EtcdKeyTreeDisplayPanel,
    private val keyValueDisplayPanel: EtcdKeyValueDisplayPanel
) : DialogWrapper(project) {

    private lateinit var panel: JPanel
    private val keyPanel = JBTextField("", 20)
    private val valuePanel = JBTextField("", 20)
    private val ttlPanel = JBTextField("", 20).apply { text = "0" }

    init {
        super.init()
        title = "Create Key"
    }

    override fun createCenterPanel(): JComponent {
        panel = panel {
            row("key: ") { cell(keyPanel) }
            row("value: ") { cell(valuePanel) }
            row("ttl(s): ") { cell(ttlPanel) }
        }
        return panel
    }

    override fun doOKAction() {
        connectionManager.getClient(etcdConnectionInfo)?.put(
            keyPanel.text,
            valuePanel.text,
            try {
                ttlPanel.text.toInt()
            } catch (e: Exception) {
                0
            }
        )
        keyTreeDisplayPanel.renderKeyTree(keyValueDisplayPanel.searchSymbol)
        close(OK_EXIT_CODE)
    }
}
