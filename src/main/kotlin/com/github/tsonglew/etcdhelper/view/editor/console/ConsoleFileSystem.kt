package com.github.tsonglew.etcdhelper.view.editor.console

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem
import org.jetbrains.annotations.NonNls

class ConsoleFileSystem : VirtualFileSystem() {

    fun openEditor(consoleVirtualFile: ConsoleVirtualFile) {
        val fileEditorManager = FileEditorManager.getInstance(consoleVirtualFile.project)
        fileEditorManager.openFile(consoleVirtualFile, true)
    }

    override fun getProtocol() = "EtcdHelper"

    override fun findFileByPath(s: @NonNls String): VirtualFile? = null

    override fun refresh(b: Boolean) {}
    override fun refreshAndFindFileByPath(s: String): VirtualFile? = null

    override fun addVirtualFileListener(virtualFileListener: VirtualFileListener) {}
    override fun removeVirtualFileListener(virtualFileListener: VirtualFileListener) {}
    override fun deleteFile(o: Any, virtualFile: VirtualFile) {}

    override fun moveFile(o: Any, virtualFile: VirtualFile, virtualFile1: VirtualFile) {}

    override fun renameFile(o: Any, virtualFile: VirtualFile, s: String) {}

    override fun createChildFile(o: Any, virtualFile: VirtualFile, s: String): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun createChildDirectory(o: Any, virtualFile: VirtualFile, s: String): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun copyFile(
        o: Any,
        virtualFile: VirtualFile,
        virtualFile1: VirtualFile,
        s: String
    ): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun isReadOnly() = false

    companion object {
        val instance = ConsoleFileSystem()
        fun getInstance(project: Project): ConsoleFileSystem {
            return project.getService(ConsoleFileSystem::class.java)
        }
    }
}
