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

class AlarmListTableManager(
        private val connectionManager: ConnectionManager,
        private var connectionInfo: EtcdConnectionInfo?
) {
    val tableName = "Alarms"
    private val sectionInfo = TableSectionInfo(
            tableName,
            arrayOf("ID", "Type"),
            arrayOf()
    )
    private val model = DefaultTableModel(sectionInfo.infoArray, sectionInfo.columns)
    val table = createAlarmListTable()

    private fun createAlarmListTable(): JBTable {
        val tableCellRenderer = DefaultTableCellRenderer().apply {
            horizontalAlignment = SwingConstants.CENTER
            border = LineBorder(JBColor.GRAY)
        }
        val table = JBTable(model).apply {
            rowSelectionAllowed = true
            setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
            setDefaultRenderer(Object::class.java, tableCellRenderer)
            tableHeader.defaultRenderer = tableCellRenderer
            autoCreateRowSorter = true
        }
        return table
    }

    fun updateAlarmInfo(connectionInfo: EtcdConnectionInfo) {
        if (this.connectionInfo == connectionInfo) {
            return
        }
        this.connectionInfo = connectionInfo
        val client = connectionManager.getClient(connectionInfo)
        val members = client?.listAlarms() ?: return
        sectionInfo.infoArray = members.map {
            arrayOf(
                    "%x".format(it.memberId),
                    it.alarmType.toString(),
            )
        }.toTypedArray()
        model.setDataVector(sectionInfo.infoArray, sectionInfo.columns)
        table.updateUI()
    }
}
