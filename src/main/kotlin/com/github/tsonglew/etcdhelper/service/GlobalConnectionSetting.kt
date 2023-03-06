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

@State(name = "EtcdHelper", storages = [Storage("etcd-helper-cache.xml")])
class GlobalConnectionSetting : PersistentStateComponent<GlobalConnectionSetting>,
    ConnectionSettings {

    private var connectionInfos: ArrayList<EtcdConnectionInfo> = arrayListOf()
    var defaultSearchSymbol = "/"
    var defaultGroupSymbol = "/"
    var defaultSearchLimit = 1000
    var enableCheatsheetPopup = true

    companion object {
        @JvmStatic
        fun getInstance(): GlobalConnectionSetting = ApplicationManager
            .getApplication()
            .getService(GlobalConnectionSetting::class.java)
    }

    override fun getState() = this
    override fun loadState(state: GlobalConnectionSetting) {
        connectionInfos = state.connectionInfos
        defaultSearchSymbol = state.defaultSearchSymbol
        defaultGroupSymbol = state.defaultGroupSymbol
        defaultSearchLimit = state.defaultSearchLimit
        enableCheatsheetPopup = state.enableCheatsheetPopup
        thisLogger().info("load state, connections: $connectionInfos,  defaultSearchSymbol: $defaultSearchSymbol, defaultGroupSymbol: $defaultGroupSymbol, defaultSearchLimit: $defaultSearchLimit, enableCheatsheetPopup: $enableCheatsheetPopup")
    }

    override fun getConnectionInfosList() = connectionInfos
}
