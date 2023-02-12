package com.github.tsonglew.etcdhelper.view.editor.console

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorPolicy
import com.intellij.openapi.fileEditor.FileEditorProvider
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import org.jdom.Element
import org.jetbrains.annotations.NonNls

class ConsoleEditorProvider : FileEditorProvider, DumbAware {
    override fun accept(project: Project, virtualFile: VirtualFile): Boolean {
        return virtualFile is ConsoleVirtualFile
    }

    override fun createEditor(project: Project, virtualFile: VirtualFile): FileEditor {
        return ConsoleEditor(virtualFile)
    }

    override fun disposeEditor(editor: FileEditor) {
        super<FileEditorProvider>.disposeEditor(editor)
    }

    override fun readState(
        sourceElement: Element,
        project: Project,
        file: VirtualFile
    ): FileEditorState {
        return super<FileEditorProvider>.readState(sourceElement, project, file)
    }

    override fun writeState(state: FileEditorState, project: Project, targetElement: Element) {
        super<FileEditorProvider>.writeState(state, project, targetElement)
    }

    override fun getEditorTypeId(): @NonNls String {
        return "EtcdHelper Console"
    }

    override fun getPolicy(): FileEditorPolicy {
        return FileEditorPolicy.HIDE_DEFAULT_EDITOR
    }
}
