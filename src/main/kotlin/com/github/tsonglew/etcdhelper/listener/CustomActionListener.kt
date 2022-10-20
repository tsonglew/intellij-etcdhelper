package com.github.tsonglew.etcdhelper.listener

import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

interface CustomActionListener {
    fun beforeAction(project: Project?) {}
    fun afterAction(project: Project?, msg: String) {}

    companion object {
        // 定义 topic
        val TOPIC = Topic.create("etcdHelperCustomAction", CustomActionListener::class.java)
    }
}
