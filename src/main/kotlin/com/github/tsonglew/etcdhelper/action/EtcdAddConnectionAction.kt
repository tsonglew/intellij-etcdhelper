package com.github.tsonglew.etcdhelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class EtcdAddConnectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("etcd add action performed")
//        val project = e.project ?: return
    }
}