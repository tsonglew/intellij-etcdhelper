package com.github.tsonglew.etcdhelper.data

import io.etcd.jetcd.KeyValue
import javax.swing.tree.DefaultMutableTreeNode

class KeyTreeNode(
    val keyValue: KeyValue,
    var keyName: String,
    val parent: DefaultMutableTreeNode
) : DefaultMutableTreeNode(keyValue) {
    val keyTreeDisplayName get() = (if (isRemoved) "(Removed)" else "") + keyName
    var isRemoved = false
}
