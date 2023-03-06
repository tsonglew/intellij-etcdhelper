/*
 * MIT License
 *
 * Copyright (c) 2022 Tsonglew
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.tsonglew.etcdhelper.table

import com.github.tsonglew.etcdhelper.api.WatchItem
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.ui.JBColor
import com.intellij.ui.table.JBTable
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.border.LineBorder
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

class WatchListTableManager(
    private val connectionManager: ConnectionManager,
    private var connectionInfo: EtcdConnectionInfo?
) {
    val tableName = "WatchList"
    private val sectionInfo = TableSectionInfo(
        tableName,
        arrayOf("Key", "isPrefix", "noPut", "noDelete", "PrevKv"),
        arrayOf()
    )
    private val model = DefaultTableModel(sectionInfo.infoArray, sectionInfo.columns)
    val table = createWatchListTable()

    private fun createWatchListTable(): JBTable {
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

    fun updateConnectionInfo(connectionInfo: EtcdConnectionInfo) {
        this.connectionInfo = connectionInfo
        val client = connectionManager.getClient(connectionInfo)
        val watchItems = client?.getWatchItems() ?: return
        watchItems.add(WatchItem())
        sectionInfo.infoArray = watchItems.map {
            arrayOf(
                it.key,
                it.isPrefix.toString(),
                it.noPut.toString(),
                it.noDelete.toString(),
                it.prevKv.toString()
            )
        }.toTypedArray()
        model.setDataVector(sectionInfo.infoArray, sectionInfo.columns)
        table.updateUI()
    }

    //    fun deleteSelectedRows() {
//        val client = connectionInfo?.let { connectionManager.getClient(it) } ?: return
//        val watchItem = selectedWatchItem ?: return
//        client.stopWatch(watchItem)
//        updateWatchListTable(connectionInfo!!)
//    }
//    private val sectionInfo = TableSectionInfo(
//        tableName,
//        arrayOf("ID", "Name", "Peer URLs", "Client URLs"),
//        arrayOf()
//    )


}
