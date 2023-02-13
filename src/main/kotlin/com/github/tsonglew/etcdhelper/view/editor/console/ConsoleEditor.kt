package com.github.tsonglew.etcdhelper.view.editor.console

import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.Nls
import java.beans.PropertyChangeListener
import javax.swing.JComponent

class ConsoleEditor(consoleVirtualFile: VirtualFile) : UserDataHolderBase(), FileEditor {
    private val consoleVirtualFile: ConsoleVirtualFile

    init {
        this.consoleVirtualFile = consoleVirtualFile as ConsoleVirtualFile
    }

    override fun getComponent(): JComponent {
        return consoleVirtualFile.consolePanel
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return consoleVirtualFile.consolePanel.cmdTextArea
    }

    override fun getName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "EtcdHelper Editor"
    }

    override fun setState(fileEditorState: FileEditorState) {}

    override fun isModified(): Boolean {
        return false
    }

    override fun isValid(): Boolean {
        return true
    }

    override fun addPropertyChangeListener(propertyChangeListener: PropertyChangeListener) {}
    override fun removePropertyChangeListener(propertyChangeListener: PropertyChangeListener) {}

    override fun getCurrentLocation(): FileEditorLocation? {
        return null
    }

    override fun getFile(): VirtualFile {
        return consoleVirtualFile
    }

    override fun dispose() {}
}
