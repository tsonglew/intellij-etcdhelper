package com.github.tsonglew.etcdhelper.listener

import com.github.tsonglew.etcdhelper.cheatsheet.CheatSheetUtil
import com.github.tsonglew.etcdhelper.common.Notifier
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class EtcdHelperListener : ProjectManagerListener {
    override fun projectOpened(project: Project) {
        val cheatsheet = CheatSheetUtil.getRandomEntry()
        Notifier.notifyInfo(
                "EtcdHelper Cheatsheet(${cheatsheet.first})",
                cheatsheet.second,
                project,
                OpenEtcdHelperSettingsAction()
        )
    }

    inner class OpenEtcdHelperSettingsAction : NotificationAction(buildString {
        append("Open EtcdHelper Settings")
    }) {
        override fun actionPerformed(e: AnActionEvent, notification: Notification) {
            ShowSettingsUtil.getInstance().showSettingsDialog(e.project, "EtcdHelper")
            notification.expire()
        }
    }
}
