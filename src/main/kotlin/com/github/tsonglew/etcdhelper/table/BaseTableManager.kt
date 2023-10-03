package com.github.tsonglew.etcdhelper.table

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.ui.JBColor
import com.intellij.ui.table.JBTable
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

interface BaseTableManager {
    val tableName: String
    val table: JBTable
    val sectionInfo: TableSectionInfo
    val model: DefaultTableModel
    var connectionInfo: EtcdConnectionInfo?
    val connectionManager: ConnectionManager

    val renderer: DefaultTableCellRenderer
        get() = DefaultTableCellRenderer().apply {
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(JBColor.GRAY)
        }

    fun getSectionInfoArr(connectionInfo: EtcdConnectionInfo): Array<Array<String>>

    fun createTable(): JBTable {
        val tableCellRenderer = renderer
        return JBTable(model).apply {
            rowSelectionAllowed = true
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setDefaultRenderer(Object::class.java, tableCellRenderer)
            tableHeader.defaultRenderer = tableCellRenderer
            autoCreateRowSorter = true
        }
    }

    fun updateConnectionInfo(connectionInfo: EtcdConnectionInfo) {
        if (this.connectionInfo == connectionInfo) {
            return
        }
        this.connectionInfo = connectionInfo
        sectionInfo.infoArray = getSectionInfoArr(connectionInfo)
        model.setDataVector(sectionInfo.infoArray, sectionInfo.columns)
        table.updateUI()
    }
}
