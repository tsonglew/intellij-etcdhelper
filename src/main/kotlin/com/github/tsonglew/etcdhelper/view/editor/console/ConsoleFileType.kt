package com.github.tsonglew.etcdhelper.view.editor.console

import com.intellij.icons.AllIcons
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NotNull

class ConsoleFileType : FileType {

    fun getCharset(@NotNull file: VirtualFile?, @NotNull content: ByteArray?) = null

    override fun getName() = "EtcdHelper.Console"

    override fun getDescription() = "EtcdHelper-Console"

    override fun getDefaultExtension() = ""

    override fun getIcon() = AllIcons.Nodes.DataColumn

    override fun isBinary() = false

    override fun isReadOnly() = true
}
