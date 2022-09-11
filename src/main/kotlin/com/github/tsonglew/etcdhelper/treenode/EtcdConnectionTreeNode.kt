package com.github.tsonglew.etcdhelper.treenode

import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import javax.swing.tree.DefaultMutableTreeNode

class EtcdConnectionTreeNode(
    private val etcdConnectionInfo: EtcdConnectionInfo?
): DefaultMutableTreeNode(etcdConnectionInfo) {

    constructor(): this(null)

    override fun toString(): String {
        return etcdConnectionInfo.toString()
    }
}
