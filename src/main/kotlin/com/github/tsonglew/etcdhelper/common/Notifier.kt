package com.github.tsonglew.etcdhelper.common

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.openapi.project.Project

object Notifier {
    fun notifyError(title: String, message: String, project: Project?) {
        Notifications.Bus.notify(
            Notification(
                "Print",
                title,
                message,
                NotificationType.ERROR,
            ),
            project
        )
    }

    fun notifyInfo(title: String, message: String, project: Project?) {
        Notifications.Bus.notify(
            Notification(
                "Print",
                title,
                message,
                NotificationType.INFORMATION,
            ),
            project
        )
    }
}
