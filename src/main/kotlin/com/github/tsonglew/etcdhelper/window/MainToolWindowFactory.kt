package com.github.tsonglew.etcdhelper.window

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class MainToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        MainToolWindow(
            project
        ).also {
            val window = MainToolWindow(project)
            val factory = ContentFactory.SERVICE.getInstance();
            val content = factory.createContent(window.rootPanel, "", false);
            toolWindow.contentManager.addContent(content);
        }
    }
}