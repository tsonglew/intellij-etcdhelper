package com.github.tsonglew.etcdhelper.tab

import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.JBSplitter
import java.awt.BorderLayout
import java.awt.FlowLayout
import javax.swing.JPanel

class EtcdKeyValueDisplayTab(
    private val project: Project,
    private val configuration: EtcdConnectionInfo,
    private var keyTreePanel: KeyTreeDisplayPanel? = null,
    private var toolbarPanel: JPanel? = null,
    private var formPanel: JPanel? = null
): JPanel() {

    init {
        initPanel()
    }

    private fun initPanel() {
        initToolBarPanel()
        initKeyTreePanel()
    }

    private fun initToolBarPanel() {
        toolbarPanel = JPanel(FlowLayout(FlowLayout.LEFT))

        JBSplitter(false, 0.35f).apply {
            dividerWidth = 2
            dividerPositionStrategy  = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
            isShowDividerControls = true
            splitterProportionKey = "etcdhelper.kv.splitter"
        }.also {
            formPanel = JPanel(BorderLayout()).apply {
                add(it, BorderLayout.NORTH)
            }
        }
    }

    private fun initKeyTreePanel() {
        keyTreePanel = KeyTreeDisplayPanel(project, this, EtcdConnectionInfo())
    }
}
