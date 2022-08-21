package com.github.tsonglew.etcdhelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import groovy.util.logging.Slf4j
import kotlin.math.log

class EtcdRefreshConnectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("etcd refresh action performed")
    }
}