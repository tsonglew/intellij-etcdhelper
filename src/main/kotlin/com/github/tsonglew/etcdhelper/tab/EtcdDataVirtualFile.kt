package com.github.tsonglew.etcdhelper.tab

import com.github.tsonglew.etcdhelper.common.EtcdConfiguration
import com.intellij.openapi.vfs.NonPhysicalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.VirtualFileSystem
import java.io.InputStream
import java.io.OutputStream

class EtcdDataVirtualFile(
    private val etcdConfiguration: EtcdConfiguration
): VirtualFile() {
    companion object{
        val virtualFileSystem: VirtualFileSystem = VirtualFileManager.getInstance().getFileSystem("etcd")
    }

    override fun getName(): String = etcdConfiguration.toString()

    override fun getFileSystem(): VirtualFileSystem = virtualFileSystem
    override fun getPath(): String {
        TODO("Not yet implemented")
    }

    override fun isWritable(): Boolean = false

    override fun isDirectory(): Boolean = false

    override fun isValid(): Boolean = true

    override fun getParent(): VirtualFile? = null

    override fun getChildren(): Array<VirtualFile> = arrayOf()

    override fun getOutputStream(requestor: Any?, newModificationStamp: Long, newTimeStamp: Long): OutputStream {
        TODO("Not yet implemented")
    }

    override fun contentsToByteArray(): ByteArray = ByteArray(0)

    override fun getTimeStamp(): Long = 0L

    override fun getLength(): Long = 0L

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) = Unit

    override fun getInputStream(): InputStream {
        TODO("Not yet implemented")
    }
}