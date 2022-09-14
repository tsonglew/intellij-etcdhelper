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
        connections.add(connectionInfo)
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
