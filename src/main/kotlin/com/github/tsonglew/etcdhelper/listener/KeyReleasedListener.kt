package com.github.tsonglew.etcdhelper.listener

import java.awt.event.KeyListener

interface KeyReleasedListener : KeyListener {
    override fun keyTyped(e: java.awt.event.KeyEvent?) {
    }

    override fun keyPressed(e: java.awt.event.KeyEvent?) {
    }
}
