package com.github.tsonglew.etcdhelper.data

import io.etcd.jetcd.KeyValue
import javax.swing.tree.DefaultMutableTreeNode

class KeyTreeNode(
    val keyValue: KeyValue,
    val keyTreeDisplayName: String,
    val parent: DefaultMutableTreeNode
) : DefaultMutableTreeNode(keyValue)
