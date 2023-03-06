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

package com.github.tsonglew.etcdhelper.action

import com.github.tsonglew.etcdhelper.listener.CustomActionListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAware
import javax.swing.Icon

abstract class CustomAction(
    private val text: String,
    description: String,
    icon: Icon
) : AnAction(text, description, icon), DumbAware {

    lateinit var action: (e: AnActionEvent) -> Unit

    protected open fun ifEnabledNotify(): Boolean = true

    override fun actionPerformed(e: AnActionEvent) {
        val messageBus = e.project?.messageBus ?: return
        val publisher = messageBus.syncPublisher(CustomActionListener.TOPIC)
        if (ifEnabledNotify()) {
            publisher.beforeAction(e.project)
        }
        action.invoke(e)
        if (ifEnabledNotify()) {
            publisher.afterAction(e.project, text)
        }
    }
}
