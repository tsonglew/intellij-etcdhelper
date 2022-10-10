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

package com.github.tsonglew.etcdhelper.service

import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.thisLogger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@State(name = "EtcdHelper", storages = [Storage("etcdHelper.xml")])
class GlobalConnectionService: PersistentStateComponent<GlobalConnectionService.Companion.State> {

    private var myState = State()

    val connections get() = myState.connections

    companion object {
        class State {
            var connectionsJson: String = "[]"
            var connections
                get() = Json.decodeFromString<ArrayList<EtcdConnectionInfo>>(connectionsJson)
                set(l) {
                    connectionsJson = Json.encodeToString(l)
                }
        }

        @JvmStatic
        fun getInstance(): GlobalConnectionService = ApplicationManager
            .getApplication()
            .getService(GlobalConnectionService::class.java)
            ?: GlobalConnectionService()
    }

    override fun getState() = myState
    override fun loadState(state: State) {
        myState = state
    }

    fun addConnection(connectionInfo: EtcdConnectionInfo) {
        val connections = myState.connections
        var oldConn: EtcdConnectionInfo? = null
        for (c in connections) {
            if (c.id.equals(connectionInfo.id)) {
                oldConn = c
                break
            }
        }
        if (oldConn == null) {
            connections.add(connectionInfo)
        } else {
            oldConn.remark = connectionInfo.remark
            oldConn.endpoints = connectionInfo.endpoints
            oldConn.username = connectionInfo.username
            oldConn.password = connectionInfo.password
        }
        myState.connections = connections
        thisLogger().info("connections after add: $connections")
    }

    fun removeConnection(connectionInfo: EtcdConnectionInfo) {
        val connections = myState.connections
        connections.remove(connectionInfo)
        myState.connections = connections
        thisLogger().info("connections after remove: $connections")
    }
}
