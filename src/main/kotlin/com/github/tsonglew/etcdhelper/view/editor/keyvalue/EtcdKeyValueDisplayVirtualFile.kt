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

package com.github.tsonglew.etcdhelper.view.editor.keyvalue

import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.common.EtcdConnectionInfo
import com.github.tsonglew.etcdhelper.common.PropertyUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.io.InputStream
import java.io.OutputStream

class EtcdKeyValueDisplayVirtualFile(
    val project: Project,
    private val name: String,
    propertyUtil: PropertyUtil,
    etcdConnectionInfo: EtcdConnectionInfo,
    connectionManager: ConnectionManager
) : VirtualFile() {

    val etcdKeyValueDisplayPanel: EtcdKeyValueDisplayPanel =
        EtcdKeyValueDisplayPanel(project, propertyUtil, etcdConnectionInfo, connectionManager)

    override fun getName() = name

    override fun getFileSystem() = EtcdKeyValueDisplayVirtualFileSystem.getInstance(project)

    override fun getPath() = name

    override fun isWritable() = true

    override fun isDirectory() = false

    override fun isValid() = true

    override fun getParent(): VirtualFile? = null

    override fun getChildren(): Array<VirtualFile> = arrayOf()

    override fun getFileType(): FileType = KeyValueDisplayFileType()

    override fun getOutputStream(
        requestor: Any?,
        newModificationStamp: Long,
        newTimeStamp: Long
    ): OutputStream {
        TODO("Not yet implemented")
    }

    override fun contentsToByteArray(): ByteArray = byteArrayOf()
    override fun getModificationStamp(): Long = timeStamp

    override fun getTimeStamp() = 0L

    override fun getLength() = 0L

    override fun refresh(asynchronous: Boolean, recursive: Boolean, postRunnable: Runnable?) {
    }

    override fun getInputStream(): InputStream {
        TODO("Not yet implemented")
    }
}
