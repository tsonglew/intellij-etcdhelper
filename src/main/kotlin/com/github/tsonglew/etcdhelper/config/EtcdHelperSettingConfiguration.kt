package com.github.tsonglew.etcdhelper.config

import com.intellij.openapi.options.Configurable
import javax.swing.JPanel

class EtcdHelperSettingConfiguration : Configurable {

    private val component = JPanel()

    override fun createComponent() = component

    override fun isModified() = false

    override fun apply() {
    }

    override fun getDisplayName() = "Etcd Helper"
}
