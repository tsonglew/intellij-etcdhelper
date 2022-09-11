package com.github.tsonglew.etcdhelper.view.editor

import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class EtcdKeyValueDisplayEditorProvider: FileEditorProvider, DumbAware {
    override fun accept(project: Project, virtualFile: VirtualFile) =
        virtualFile is EtcdKeyValueDisplayVirtualFile

    override fun createEditor(project: Project, file: VirtualFile) = EtcdKeyValueDisplayEditor(
        file as EtcdKeyValueDisplayVirtualFile
    )

    override fun getEditorTypeId() = "Etcd Key-Value"

    override fun getPolicy() = FileEditorPolicy.HIDE_DEFAULT_EDITOR
}
