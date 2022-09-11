package com.github.tsonglew.etcdhelper.tab

import com.github.tsonglew.etcdhelper.common.EtcdClient
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.view.render.ConnectionTreeCellRenderer
import com.intellij.openapi.project.Project
import com.intellij.ui.treeStructure.Tree
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JPanel

class KeyTreeDisplayPanel(
    private val project: Project,
    private val keyValueDisplayTab: EtcdKeyValueDisplayTab,
    private val etcdConnectionInfo: EtcdConnectionInfo
): JPanel() {
    init {
        val etcdClient = EtcdClient().apply {
            init(
                etcdConnectionInfo.endpoints.split(",").toTypedArray(),
                etcdConnectionInfo.username,
                etcdConnectionInfo.password
            )
        }
        val keys = etcdClient.getByPrefix("/", null);
        val keyTree = Tree().apply {
            cellRenderer = ConnectionTreeCellRenderer()
            addMouseListener(object : MouseListener{
                override fun mouseClicked(e: MouseEvent?) {
                    println("mouse clicked")
                }

                override fun mousePressed(e: MouseEvent?) {
                    println("mouse pressed")
                }

                override fun mouseReleased(e: MouseEvent?) {
                    println("mouse released")
                }

                override fun mouseEntered(e: MouseEvent?) {
                    println("mouse entered")
                }

                override fun mouseExited(e: MouseEvent?) {
                    println("mouse exited")
                }

            })
        }
    }
}
