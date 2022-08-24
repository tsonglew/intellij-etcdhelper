package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.common.EtcdClientManager
import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import com.github.tsonglew.etcdhelper.data.ConnectionManager
import com.github.tsonglew.etcdhelper.dialog.EtcdAddConnectionDialog
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class EtcdAddConnectionAction : AnAction(
    "Add Connection",
    "Add Connection",
    AllIcons.General.Add
) {
    override fun actionPerformed(e: AnActionEvent) {
        println("etcd add action performed")
        e.project?.also {p ->
          EtcdAddConnectionDialog(p).also {
              if (it.showAndGet()) {
                  println("show and get")
                  EtcdClientManager.mainToolWindow.addTreeNode(
                      it.toEtcdConfiguration()
                  )
              }
          }
        }
    }
}