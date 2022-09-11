package com.github.tsonglew.etcdhelper.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import java.util.function.Consumer
import javax.swing.Icon

abstract class CustomAction(
    text: String,
    description: String,
    icon: Icon
): AnAction(text, description, icon), DumbAware  {

    lateinit var action: (e: AnActionEvent) -> Unit

    override fun actionPerformed(e: AnActionEvent) = action.invoke(e)
}
