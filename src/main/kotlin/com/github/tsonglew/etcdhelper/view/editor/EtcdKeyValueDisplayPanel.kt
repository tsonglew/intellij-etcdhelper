package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.LoadingDecorator
import com.intellij.openapi.ui.Splitter
import com.intellij.ui.JBSplitter
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextField
import com.intellij.util.ui.JBUI
import io.etcd.jetcd.KeyValue
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.ScrollPaneConstants

class EtcdKeyValueDisplayPanel(
    private val project: Project,
    private val connectionInfo: EtcdConnectionInfo,
    private val connectionManager: ConnectionManager
): JPanel(), Disposable {

    companion object {
        private const val defaultGroupBySymbol = ":"
        private const val defaultSearchSymbol = "*"
    }

    /**
     * a panel to display tool bar
     */
    private val keyToolBarPanel = JPanel(FlowLayout(FlowLayout.LEFT))
    private var valueDisplayPanel: EtcdValueDisplayPanel? = null
    private val splitterContainer = JBSplitter(false, 0.35f).apply {
        secondComponent = JPanel().apply { minimumSize= Dimension(100, 100) }
        dividerWidth = 2
        dividerPositionStrategy = Splitter.DividerPositionStrategy.KEEP_FIRST_SIZE
        isShowDividerControls = true
        splitterProportionKey = "etcdhelper.keyValue.splitter"
    }
    private val formPanel = JPanel(BorderLayout()).apply {
        add(keyToolBarPanel, BorderLayout.NORTH)
        add(splitterContainer, BorderLayout.CENTER)
    }

    private lateinit var searchTextField: SearchTextField
    private lateinit var groupTextField: JBTextField
    val searchSymbol: String
        get() {
            return searchTextField.text
        }
    val groupSymbol: String
        get() {
            return groupTextField.text
        }

    private lateinit var keyTreeDisplayPanel: EtcdKeyTreeDisplayPanel

    init {
        layout = BorderLayout()
        add(formPanel)

        ApplicationManager.getApplication().invokeLater(this::initKeyToolBarPanel)
        ApplicationManager.getApplication().invokeLater(this::initKeyTreePanel)
    }

    override fun dispose() {
    }

    private fun initKeyToolBarPanel() {
        keyToolBarPanel.add(createSearchBox())
        keyToolBarPanel.add(createGroupByPanel())
    }

    private fun initKeyTreePanel() {
        keyTreeDisplayPanel = EtcdKeyTreeDisplayPanel(
            project,
            this,
            splitterContainer,
            connectionInfo,
            connectionManager,
            this::renderValueDisplayPanel
        ).apply {
            renderKeyTree(searchSymbol, groupSymbol)
        }
    }

    private fun createSearchBox() = JPanel().apply {
        add(JLabel("Search: "))
        add(SearchTextField().apply {
            searchTextField = this
            addKeyboardListener(object : KeyListener {
                override fun keyTyped(e: KeyEvent?) {
                    println("search box key typed")
                }

                override fun keyPressed(e: KeyEvent?) {
                    println("search box key pressed")
                }

                override fun keyReleased(e: KeyEvent?) {
                    if (e?.keyCode == KeyEvent.VK_ENTER) {
                        keyTreeDisplayPanel.resetPageIndex()
                        keyTreeDisplayPanel.renderKeyTree(searchSymbol, groupSymbol)
                        println("current search symbol: $searchSymbol")
                    }
                }
            })
        })
    }

    private fun createGroupByPanel() = JPanel().apply {
        border = JBUI.Borders.empty()
        add(JLabel("Group by: "))
        add(JBTextField("test group").apply {
            groupTextField = this
            addKeyListener(object : KeyListener {
                override fun keyTyped(e: KeyEvent?) {
                    println("group key typed")
                }

                override fun keyPressed(e: KeyEvent?) {
                    println("group key pressed")
                }

                override fun keyReleased(e: KeyEvent?) {
                    // TODO: rerender after key released
                    println("group key released, current group symbol: $groupSymbol")
                }
            })
        })
    }

    /**
     * render value display panel when key tree is clicked
     */
    private fun renderValueDisplayPanel(key: String) {
        val valueDisplayScrollPanel = JBScrollPane(valueDisplayPanel).apply {
        horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
        }
        val loadingDecorator = LoadingDecorator(valueDisplayScrollPanel, this, 0)
        splitterContainer.secondComponent = loadingDecorator.component
        valueDisplayPanel = EtcdValueDisplayPanel(
            BorderLayout(),
            project,
            this,
            key,
            connectionInfo,
            connectionManager,
            loadingDecorator
        )
    }
}
