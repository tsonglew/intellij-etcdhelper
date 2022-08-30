package com.github.tsonglew.etcdhelper.treenode

import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import javax.swing.tree.DefaultMutableTreeNode

class EtcdConnectionTreeNode(
    private val etcdConfiguration: EtcdConfiguration?
): DefaultMutableTreeNode(etcdConfiguration) {

    constructor(): this(null)

    override fun toString(): String {
        return etcdConfiguration.toString()
    }
}
