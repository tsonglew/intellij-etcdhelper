/*
 * MIT License
 *
 * Copyright (c) 2022 Tsonglew
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.tsonglew.etcdhelper.listener

import com.github.tsonglew.etcdhelper.cheatsheet.CheatSheetUtil
import com.github.tsonglew.etcdhelper.common.Notifier
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener
import com.intellij.openapi.vfs.LocalFileSystem

class EtcdHelperListener : ProjectManagerListener {

    private fun registerVirtualFileListeners() {
        LocalFileSystem.getInstance().addVirtualFileListener(CustomVirtualFileListener())
    }

    override fun projectOpened(project: Project) {

        registerVirtualFileListeners()

        val cheatsheet = CheatSheetUtil.getRandomEntry()
        Notifier.notifyInfo(
                "EtcdHelper Cheatsheet",
                "<h3>${cheatsheet.first}</h3>${cheatsheet.second}",
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
