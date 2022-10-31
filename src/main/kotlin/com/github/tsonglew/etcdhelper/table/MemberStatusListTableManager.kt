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

class MemberStatusListTableManager(
        private val connectionManager: ConnectionManager,
        private var connectionInfo: EtcdConnectionInfo?
) {
    val tableName = "Status"
    private val sectionInfo = TableSectionInfo(
            tableName,
            arrayOf("ID", "leader", "dbSize(B)", "raftIndex", "raftTerm"),
            arrayOf()
    )
    private val model = DefaultTableModel(sectionInfo.infoArray, sectionInfo.columns)
    val table = createMemberStatusListTable()

    private fun createMemberStatusListTable(): JBTable {
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

    fun updateMemberStatusInfo(connectionInfo: EtcdConnectionInfo) {
        if (this.connectionInfo == connectionInfo) {
            return
        }
        this.connectionInfo = connectionInfo
        val client = connectionManager.getClient(connectionInfo)
        val status = client?.listMemberStatus() ?: return
        sectionInfo.infoArray = status.map {
            arrayOf(
                    "%x".format(it.header.memberId),
                    "%x".format(it.leader),
                    "%d".format(it.dbSize),
                    "%d".format(it.raftIndex),
                    "%d".format(it.raftTerm)
            )
        }.toTypedArray()
        model.setDataVector(sectionInfo.infoArray, sectionInfo.columns)
        table.updateUI()
    }
}
