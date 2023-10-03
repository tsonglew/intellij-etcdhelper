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

package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.client.RpcClient
import com.github.tsonglew.etcdhelper.client.impl.EtcdClient
import com.github.tsonglew.etcdhelper.tree.node.EtcdConnectionTreeNode
import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFile
import com.github.tsonglew.etcdhelper.window.MainToolWindow
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class ConnectionManager(
    private val mainToolWindow: MainToolWindow,
    private val project: Project,
    private val propertyUtil: PropertyUtil,
    private val connectionTree: Tree
) : Disposable {

    /**
     * map of endpoints to {@link EtcdClient}
     */
    private val connectionMap = hashMapOf<String, RpcClient>()
    private val connectionEditorMap = hashMapOf<String, EtcdKeyValueDisplayVirtualFile>()

    companion object {
        @JvmStatic
        fun getInstance(
            mainToolWindow: MainToolWindow,
            project: Project,
            propertyUtil: PropertyUtil,
            connectionTree: Tree
        ) = project.getService(ConnectionManager::class.java)
            ?: ConnectionManager(mainToolWindow, project, propertyUtil, connectionTree)
    }

    fun getClient(etcdConnectionInfo: EtcdConnectionInfo): RpcClient {
        if (etcdConnectionInfo.id!! !in connectionMap ||
            connectionMap[etcdConnectionInfo.id!!] == null
        )
            connectionMap[etcdConnectionInfo.id!!] = EtcdClient(etcdConnectionInfo, project)
        return connectionMap[etcdConnectionInfo.id!!]!!
    }

    /**
     * init saved connections
     */
    fun initConnections(connectionTree: Tree) {
        connectionTree.isRootVisible = false
        propertyUtil.getConnections().forEach {
            addConnectionToList(it)
        }
    }

    /**
     * add a new etcd connection to the model
     */
    fun addConnectionToList(etcdConnectionInfo: EtcdConnectionInfo) {
        (connectionTree.model as DefaultTreeModel).apply {
            (root as EtcdConnectionTreeNode).apply {
                var found = false
                for (i in 0 until getChildCount(this)) {
                    ((getChildAt(i) as EtcdConnectionTreeNode).userObject as EtcdConnectionInfo).apply {
                        if (id == etcdConnectionInfo.id) {
                            endpoints = etcdConnectionInfo.endpoints
                            username = etcdConnectionInfo.username
                            name = etcdConnectionInfo.name
                            found = true
                        }
                    }
                }
                if (!found) {
                    add(EtcdConnectionTreeNode(etcdConnectionInfo, this@ConnectionManager))
                }
                connectionMap[etcdConnectionInfo.id!!] = EtcdClient(etcdConnectionInfo, project)
            }
            reload()
        }
    }

    fun getVirtualFile(connectionInfo: EtcdConnectionInfo): EtcdKeyValueDisplayVirtualFile {
        if (!connectionEditorMap.containsKey(connectionInfo.toString())) {
            connectionEditorMap[connectionInfo.toString()] = EtcdKeyValueDisplayVirtualFile(
                mainToolWindow,
                project,
                connectionInfo.endpoints,
                propertyUtil,
                connectionInfo,
                this
            )
        }
        return connectionEditorMap[connectionInfo.toString()]!!
    }

    /**
     * remove the selected connection on connection tree
     */
    fun removeSelectedConnection() {
        val connectionTreeModel = connectionTree.model as DefaultTreeModel
        val root = connectionTreeModel.root as DefaultMutableTreeNode
        connectionTree.selectionPaths?.forEach {
            thisLogger().info("remove tree node: ${it.path}")
            root.remove(it.lastPathComponent as MutableTreeNode)
            propertyUtil.removeConnection((it.lastPathComponent as DefaultMutableTreeNode).userObject as EtcdConnectionInfo)
        }
        connectionTreeModel.reload()
    }

    override fun dispose() {
        connectionMap.forEach {
            it.value.close()
        }
    }
}
