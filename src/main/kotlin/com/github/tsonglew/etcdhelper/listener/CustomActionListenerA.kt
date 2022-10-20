package com.github.tsonglew.etcdhelper.listener

import com.github.tsonglew.etcdhelper.common.Notifier
import com.intellij.openapi.project.Project

class CustomActionListenerA : CustomActionListener {
    override fun beforeAction(project: Project?) {
        println("beforeActionA")
    }

    override fun afterAction(project: Project?, msg: String) {
        Notifier.notifyInfo("EtcdHelper", "$msg success", project)
    }
}
