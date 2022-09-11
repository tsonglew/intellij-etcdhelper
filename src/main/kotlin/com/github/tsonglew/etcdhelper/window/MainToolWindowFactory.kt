package com.github.tsonglew.etcdhelper.window

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.apply {
            addContent(
                ApplicationManager
                    .getApplication()
                    .getService(ContentFactory::class.java)
                    .createContent(
                        MainToolWindow(project, toolWindow).content,
                        "",
                        false
                    )
            )
        }
    }
}
