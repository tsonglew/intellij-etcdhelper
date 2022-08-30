package com.github.tsonglew.etcdhelper.tab

import com.github.tsonglew.etcdhelper.common.EtcdClient
import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import com.github.tsonglew.etcdhelper.render.KeyTreeCellRenderer
import com.intellij.openapi.project.Project
import com.intellij.ui.ColoredTreeCellRenderer
import com.intellij.ui.treeStructure.Tree
import javax.swing.JPanel

class KeyTreeDisplayPanel(
    private val project: Project,
    private val keyValueDisplayTab: EtcdKeyValueDisplayTab,
    private val etcdConfiguration: EtcdConfiguration
): JPanel() {
    init {
        val etcdClient = EtcdClient().apply {
            init(
                etcdConfiguration.endpoints.split(",").toTypedArray(),
                etcdConfiguration.username,
                etcdConfiguration.password
            )
        }
        val keys = etcdClient.getByPrefix("/", null);
        val keyTree = Tree().apply {
            cellRenderer = KeyTreeCellRenderer()
        }
    }
}
