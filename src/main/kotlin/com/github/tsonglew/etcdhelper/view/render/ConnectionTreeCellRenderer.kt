package com.github.tsonglew.etcdhelper.view.render

import com.intellij.icons.AllIcons
import com.intellij.ui.ColoredTreeCellRenderer
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class ConnectionTreeCellRenderer: ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        val node = value as DefaultMutableTreeNode
        val userObj = node.userObject
        append(node.toString())
        icon = AllIcons.Debugger.Db_array
    }
}
