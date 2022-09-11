package com.github.tsonglew.etcdhelper.common

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

class ConnectionManager(
    private val project: Project,
    private val propertyUtil: PropertyUtil
): Disposable {
    private val connectionTreeRoot= DefaultMutableTreeNode()
    private val connectionTreeModel = DefaultTreeModel(connectionTreeRoot)
    private val connectionMap = hashMapOf<String, EtcdClient>()

    companion object {
        fun getInstance(project: Project, propertyUtil: PropertyUtil) =
            project.getService(ConnectionManager::class.java) ?: ConnectionManager(project, propertyUtil)
    }

    /**
     * init saved connections
     */
    fun initConnections(connectionTree: Tree) {
        propertyUtil.getConnections().forEach {
            addConnectionToList(it)
        }
        connectionTree.model = connectionTreeModel
        connectionTree.isRootVisible = false
    }

    /**
     * add a new etcd connection to model
     */
    fun addConnectionToList( etcdConnectionInfo: EtcdConnectionInfo) {
        connectionTreeModel.apply {
            (root as DefaultMutableTreeNode).apply {
                add(DefaultMutableTreeNode(etcdConnectionInfo))
                connectionMap[etcdConnectionInfo.endpoints] = EtcdClient().apply { init(etcdConnectionInfo) }
            }
            reload()
        }
    }

    override fun dispose() {
        connectionMap.forEach {
            it.value.close()
        }
    }
}
