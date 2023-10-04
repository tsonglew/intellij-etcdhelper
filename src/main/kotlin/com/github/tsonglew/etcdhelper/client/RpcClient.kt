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

package com.github.tsonglew.etcdhelper.client

import com.github.tsonglew.etcdhelper.api.RoleItem
import com.github.tsonglew.etcdhelper.api.UserItem
import com.github.tsonglew.etcdhelper.api.WatchItem
import io.etcd.jetcd.KeyValue
import io.etcd.jetcd.Watch.Watcher
import io.etcd.jetcd.cluster.Member
import io.etcd.jetcd.maintenance.AlarmMember
import io.etcd.jetcd.maintenance.StatusResponse

interface RpcClient {
    fun get(key: String): List<KeyValue>
    fun getByPrefix(key: String, limit: Int?): List<KeyValue>
    fun close()
    fun delete(key: String): Boolean
    fun put(key: String, value: String, ttlSecs: Int): Boolean
    fun getLeaseInfo(leaseId: Long): Long
    fun deleteByPrefix(key: String): Boolean
    fun listClusterMembers(): MutableList<Member>?
    fun listAlarms(): MutableList<AlarmMember>
    fun listMemberStatus(): MutableList<StatusResponse>
    fun startWatch(watchItem: WatchItem): Watcher
    fun stopWatch(watchItem: WatchItem)
    fun getWatchItems(): MutableList<WatchItem>
    fun isActive(): Boolean
    suspend fun listUsers(): List<UserItem>
    suspend fun listRoles(): List<RoleItem>
}
