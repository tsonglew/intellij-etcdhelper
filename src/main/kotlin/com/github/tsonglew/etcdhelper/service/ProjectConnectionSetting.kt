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
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.StoragePathMacros
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project

@State(name = "EtcdHelper", storages = [Storage(StoragePathMacros.WORKSPACE_FILE)])
class ProjectConnectionSetting : PersistentStateComponent<ProjectConnectionSetting>, ConnectionSettings {

    var connectionInfos: ArrayList<EtcdConnectionInfo> = arrayListOf()
    override fun getConnectionInfosList(): ArrayList<EtcdConnectionInfo> = connectionInfos

    companion object {
        @JvmStatic
        fun getInstance(project: Project): ProjectConnectionSetting =
                project.getService(ProjectConnectionSetting::class.java)
    }

    override fun getState() = this
    override fun loadState(state: ProjectConnectionSetting) {
        connectionInfos = state.connectionInfos
        thisLogger().info("load state: $connectionInfos")
    }
}
