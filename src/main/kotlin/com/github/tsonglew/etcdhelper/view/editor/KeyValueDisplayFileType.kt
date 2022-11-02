package com.github.tsonglew.etcdhelper.view.editor

import com.github.tsonglew.etcdhelper.icon.EtcdIcons
import com.intellij.openapi.fileTypes.FileType

class KeyValueDisplayFileType : FileType {
    override fun getName() = "EtcdHelper type"

    override fun getDescription() = "A EtcdHelper file tab"

    override fun getDefaultExtension() = ""

    override fun getIcon() = EtcdIcons.ETCD_ICON

    override fun isBinary() = false
}
