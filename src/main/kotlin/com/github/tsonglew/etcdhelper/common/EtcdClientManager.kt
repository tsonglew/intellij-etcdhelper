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

package com.github.tsonglew.etcdhelper.common

import com.github.tsonglew.etcdhelper.window.MainToolWindow
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * @author tsonglew
 */
object EtcdClientManager {
    private val CONN_MAP = ConcurrentHashMap<String, EtcdClient>()
    fun addConn(project: Project, endpoints: String, user: String, password: String): EtcdClient? {
        thisLogger().info("add etcd connection: ")
        thisLogger().info("endpoints: $endpoints")
        thisLogger().info("user: $user")
        thisLogger().info("password: $password")
        CONN_MAP.computeIfAbsent(endpoints) { k: String ->
            val c = EtcdClient(project)
            c.init(k.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray(), null, null)
            c
        }
        return CONN_MAP[endpoints]
    }

    lateinit var mainToolWindow: MainToolWindow
}
