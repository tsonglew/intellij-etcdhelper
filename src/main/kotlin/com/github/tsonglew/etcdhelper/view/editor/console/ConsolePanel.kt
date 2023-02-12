package com.github.tsonglew.etcdhelper.view.editor.console

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.ActionToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.JBSplitter
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import javax.swing.JPanel

class ConsolePanel(
    private val project: Project,
    val connectionInfo: EtcdConnectionInfo,
    val connectionManager: ConnectionManager
) : JPanel() {
    private var container: JBSplitter? = null
    var cmdTextArea: ConsoleCommandTextArea? = null

    init {
        layout = BorderLayout()
    }

    private fun init() {
        val resultArea = JBTextArea().apply {
            isEditable = false
            lineWrap = true
            wrapStyleWord = true
            margin = JBUI.insetsLeft(10)
        }
        val executeResultScrollPane = JBScrollPane(resultArea).apply {
            horizontalScrollBarPolicy = JBScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        }
        val actions = DefaultActionGroup()
        val actionToolbar: ActionToolbar =
            ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actions, false)

        val executeResultPanel = JPanel(BorderLayout())
        executeResultPanel.add(executeResultScrollPane, BorderLayout.CENTER)
        executeResultPanel.add(actionToolbar.component, BorderLayout.EAST)

        cmdTextArea = ConsoleCommandTextArea(resultArea, connectionManager)
        val cmdPanel = JPanel(BorderLayout())
        cmdPanel.add(cmdTextArea)
        val cmdScrollPane = JBScrollPane(cmdPanel)
        add(
            JBSplitter(true, 0.6f).apply {
                dividerPositionStrategy = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
                firstComponent = cmdScrollPane
                secondComponent = executeResultPanel
            },
            BorderLayout.CENTER
        )
    }
}
