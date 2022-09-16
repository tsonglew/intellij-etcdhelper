package com.github.tsonglew.etcdhelper.view.render

import com.github.tsonglew.etcdhelper.data.KeyTreeNode
import com.intellij.icons.AllIcons
import com.intellij.ui.ColoredTreeCellRenderer
import javax.swing.JTree
import javax.swing.tree.DefaultMutableTreeNode

class KeyTreeCellRenderer: ColoredTreeCellRenderer() {
    override fun customizeCellRenderer(
        tree: JTree,
        value: Any?,
        selected: Boolean,
        expanded: Boolean,
        leaf: Boolean,
        row: Int,
        hasFocus: Boolean
    ) {
        append(
            when (value) {
                is KeyTreeNode -> value.keyTreeDisplayName
                is DefaultMutableTreeNode -> value.userObject.toString()
                else -> value.toString()
            }
        )
        icon = when {
            (row == 0) -> AllIcons.Debugger.Db_array
            (leaf) -> AllIcons.Debugger.Value
            else -> AllIcons.Nodes.Folder
        }
    }
}
