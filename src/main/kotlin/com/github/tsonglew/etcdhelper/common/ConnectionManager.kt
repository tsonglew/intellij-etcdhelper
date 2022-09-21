package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.view.editor.EtcdKeyValueDisplayVirtualFile
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.MutableTreeNode

class ConnectionManager(
    private val project: Project,
    private val propertyUtil: PropertyUtil,
    private val connectionTree: Tree
): Disposable {

    /**
     * map of endpoints to {@link EtcdClient}
     */
    private val connectionMap = hashMapOf<String, EtcdClient>()
    private val connectionEditorMap = hashMapOf<String, EtcdKeyValueDisplayVirtualFile>()

    companion object {
        @JvmStatic
        fun getInstance(
            project: Project,
            propertyUtil: PropertyUtil,
            connectionTree: Tree
        ) = project.getService(ConnectionManager::class.java)
            ?: ConnectionManager(project, propertyUtil, connectionTree)
    }

    fun getClient(etcdConnectionInfo: EtcdConnectionInfo) = connectionMap[etcdConnectionInfo.endpoints]

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
     * add a new etcd connection to model
     */
    fun addConnectionToList(etcdConnectionInfo: EtcdConnectionInfo) {
        (connectionTree.model as DefaultTreeModel).apply {
            (root as DefaultMutableTreeNode).apply {
                add(DefaultMutableTreeNode(etcdConnectionInfo))
                connectionMap[etcdConnectionInfo.endpoints] = EtcdClient().apply { init(etcdConnectionInfo) }
            }
            reload()
        }
    }

    fun getVirtualFile(connectionInfo: EtcdConnectionInfo): EtcdKeyValueDisplayVirtualFile {
        if (!connectionEditorMap.containsKey(connectionInfo.toString())) {
            connectionEditorMap[connectionInfo.toString()] = EtcdKeyValueDisplayVirtualFile(
                project,
                connectionInfo.endpoints,
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
