package com.github.tsonglew.etcdhelper.view.editor

import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class EtcdKeyValueDisplayEditor(
    private val virtualFile: EtcdKeyValueDisplayVirtualFile
): UserDataHolderBase(), FileEditor {
    override fun dispose() {
        // TODO: remove connection -> editor mapping
        thisLogger().info("dispose etcd key value display editor")
    }

    override fun getComponent() = virtualFile.etcdKeyValueDisplayPanel

    override fun getPreferredFocusedComponent(): JComponent? = null

    override fun getName() = "Etcd Editor"

    override fun setState(state: FileEditorState) {
    }

    override fun isModified() = false

    override fun isValid() = true

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
    }

    override fun getCurrentLocation(): FileEditorLocation? = null
    override fun getFile(): VirtualFile = virtualFile
}
