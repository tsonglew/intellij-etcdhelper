package com.github.tsonglew.etcdhelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RefreshAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("etcd refresh action performed")
    }
}
