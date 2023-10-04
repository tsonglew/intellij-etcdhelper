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

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import io.etcd.jetcd.auth.Permission
import javax.swing.table.DefaultTableModel

class RoleTableManager(
    override val connectionManager: ConnectionManager,
    override var connectionInfo: EtcdConnectionInfo?
) : BaseTableManager {
    override val tableName = "Roles"
    override val sectionInfo = TableSectionInfo(
        tableName, arrayOf("Roles", "Permissions"),
        arrayOf()
    )
    override val model = DefaultTableModel(sectionInfo.infoArray, sectionInfo.columns)
    override val table = createTable()

    override suspend fun getSectionInfoArr(connectionInfo: EtcdConnectionInfo): Array<Array<String>> {
        val client = connectionManager.getClient(connectionInfo)
        val roles = client.listRoles()
        var result = arrayOf<Array<String>>()
        roles.forEach {
            it.permissions.forEach { p ->
                result += arrayOf(it.role, p.toColumnString())
            }
        }
        return result
    }

    private fun Permission.toColumnString() =
        "Type: ${this.permType}, Key: ${this.key}, rangeEnd: ${
            this
                .rangeEnd
        }"
}
