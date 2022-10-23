package com.github.tsonglew.etcdhelper.listener

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.vfs.VirtualFileEvent
import com.intellij.openapi.vfs.VirtualFileListener

class CustomVirtualFileListener : VirtualFileListener {
    override fun contentsChanged(event: VirtualFileEvent) {
        val file = event.file
        thisLogger().info("contentsChanged: ${file.name}")
    }
}
