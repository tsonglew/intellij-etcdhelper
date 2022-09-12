package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.InputStream
import java.io.OutputStream

class EtcdKeyValueDisplayVirtualFile(
    val project: Project,
    private val name: String,
    private val etcdConnectionInfo: EtcdConnectionInfo,
    private val connectionManager: ConnectionManager
): VirtualFile() {

    val etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel =
        EtcdKeyValueDisplayPanel(project, etcdConnectionInfo, connectionManager)

    override fun getName() = name

    override fun getFileSystem() = EtcdKeyValueDisplayVirtualFileSystem.getInstance(project)

    override fun getPath() = name

    override fun isWritable() = true

    override fun isDirectory() = false

    override fun isValid() = true

    override fun getParent(): VirtualFile? = null

    override fun getChildren(): Array<VirtualFile> = arrayOf();

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        TODO("Not yet implemented")
    }

    override fun contentsToByteArray(): ByteArray = byteArrayOf()

    override fun getTimeStamp() = 0L

    override fun getLength() = 0L

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
    }

    override fun getInputStream(): InputStream {
        TODO("Not yet implemented")
    }
}