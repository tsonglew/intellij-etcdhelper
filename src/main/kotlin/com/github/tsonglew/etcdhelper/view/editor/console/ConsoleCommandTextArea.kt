package com.github.tsonglew.etcdhelper.view.editor.console

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBTextArea
import com.intellij.util.ui.JBUI
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JTextPane
import javax.swing.text.*
import javax.swing.text.rtf.RTFEditorKit

class ConsoleCommandTextArea(resultArea: JBTextArea, connectionManager: ConnectionManager) :
    JTextPane() {
    private val keyAttr: MutableAttributeSet
    private val normalAttr: MutableAttributeSet
    private val inputAttributes: MutableAttributeSet = RTFEditorKit().inputAttributes
    private val fontSize = 14
    private var styleCxt: StyleContext = StyleContext()
    private var styleDoc: DefaultStyledDocument = DefaultStyledDocument(styleCxt)
    private var db = 0

    init {
        document = styleDoc
        autoscrolls = true
        margin = JBUI.insetsLeft(15)
        val ths = this
        this.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(ke: KeyEvent) {
                executeCommand(ke, ths, connectionManager, resultArea)
            }

            override fun keyReleased(event: KeyEvent) {
            }
        })

        keyAttr = SimpleAttributeSet()
        StyleConstants.setForeground(keyAttr, Color(49, 140, 175))
        StyleConstants.setFontSize(keyAttr, fontSize)
        StyleConstants.setBold(keyAttr, false)

        normalAttr = SimpleAttributeSet()
        StyleConstants.setBold(normalAttr, false)
        StyleConstants.setForeground(normalAttr, JBColor.BLACK)
        StyleConstants.setFontSize(normalAttr, fontSize)
    }

    override fun paint(g: Graphics) {
        super.paint(g)
        StyleConstants.setFontSize(getInputAttributes(), fontSize)
        drawLineStarter(g)
    }

    private fun drawLineStarter(g: Graphics) {
        val element: Element = styleDoc.defaultRootElement
        val rows = element.elementCount
        g.color = JBColor.BLACK
        g.font = Font(font.name, font.style, fontSize)
        val lineHeight = g.fontMetrics.height
        for (row in 0 until rows) {
            g.drawString(">", 2, getPositionY(row + 1, lineHeight))
        }
    }

    private fun getPositionY(row: Int, lineHeight: Int): Int {
        return row * lineHeight - 4
    }

    private fun executeCommand(
        ke: KeyEvent,
        ths: ConsoleCommandTextArea,
        connectionManager: ConnectionManager,
        resultArea: JBTextArea
    ) {
    }
}
