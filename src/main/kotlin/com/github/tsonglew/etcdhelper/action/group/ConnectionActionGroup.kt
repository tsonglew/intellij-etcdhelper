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

package com.github.tsonglew.etcdhelper.action.group

import com.github.tsonglew.etcdhelper.action.ConnectionAddAction
import com.github.tsonglew.etcdhelper.action.ConnectionDeleteAction
import com.github.tsonglew.etcdhelper.action.EditAction
import com.github.tsonglew.etcdhelper.common.ActionTypeEnum
import com.github.tsonglew.etcdhelper.common.ConnectionManager
import com.github.tsonglew.etcdhelper.tree.ConnectionTree
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project

class ConnectionActionGroup(
    project: Project,
    connectionManager: ConnectionManager,
    connectionTree: ConnectionTree,
    vararg actionType: ActionTypeEnum
) : DefaultActionGroup() {
    init {
        actionType.forEach {
            when (it) {
                ActionTypeEnum.ADD -> add(
                    ConnectionAddAction.create(
                        project,
                        connectionManager
                    )
                )

                ActionTypeEnum.DELETE -> add(
                    ConnectionDeleteAction.create(
                        project,
                        connectionTree,
                        connectionManager
                    )
                )

                ActionTypeEnum.EDIT -> add(
                    EditAction.create(
                        project,
                        connectionTree,
                        connectionManager
                    )
                )

                ActionTypeEnum.SEPARATOR -> addSeparator()
            }
        }
    }
}
