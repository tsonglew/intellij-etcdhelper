package com.github.tsonglew.etcdhelper.view.editor

import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileSystem

class EtcdKeyValueDisplayVirtualFileSystem: VirtualFileSystem() {

    companion object {
        fun getInstance(project: Project) = project.getService(EtcdKeyValueDisplayVirtualFileSystem::class.java)
            ?:EtcdKeyValueDisplayVirtualFileSystem()
    }

    fun openEditor(f: EtcdKeyValueDisplayVirtualFile) = FileEditorManager.getInstance(f.project).openFile(f, true)

    override fun getProtocol() = "Etcd"

    override fun findFileByPath(path: String): VirtualFile?  = null

    override fun refresh(asynchronous: Boolean) {}

    override fun refreshAndFindFileByPath(path: String): VirtualFile? = null

    override fun addVirtualFileListener(listener: VirtualFileListener) {}

    override fun removeVirtualFileListener(listener: VirtualFileListener) {}

    override fun deleteFile(requestor: Any?, vFile: VirtualFile) {}

    override fun moveFile(requestor: Any?, vFile: VirtualFile, newParent: VirtualFile) {}

    override fun renameFile(requestor: Any?, vFile: VirtualFile, newName: String) {}
    override fun createChildFile(requestor: Any?, vDir: VirtualFile, fileName: String): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun createChildDirectory(requestor: Any?, vDir: VirtualFile, dirName: String): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun copyFile(
        requestor: Any?,
        virtualFile: VirtualFile,
        newParent: VirtualFile,
        copyName: String
    ): VirtualFile {
        TODO("Not yet implemented")
    }

    override fun isReadOnly() = false
}
