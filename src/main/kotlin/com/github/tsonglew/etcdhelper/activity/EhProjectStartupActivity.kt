package com.github.tsonglew.etcdhelper.activity

import com.github.tsonglew.etcdhelper.cheatsheet.CheatSheetUtil
import com.github.tsonglew.etcdhelper.common.Notifier
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.github.tsonglew.etcdhelper.listener.CustomVirtualFileListener
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.vfs.LocalFileSystem

class EhProjectStartupActivity : StartupActivity {

    override fun runActivity(project: Project) {
        registerVirtualFileListeners()

        if (PropertyUtil().connectionService.enableCheatsheetPopup) {
            val cheatsheet = CheatSheetUtil.getRandomEntry()
            Notifier.notifyInfo(
                "Etcd Cheatsheet(${cheatsheet.first})",
                cheatsheet.second,
                project,
                OpenEtcdHelperSettingsAction()
            )
        }
    }

    private fun registerVirtualFileListeners() {
        LocalFileSystem.getInstance().addVirtualFileListener(CustomVirtualFileListener())
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
