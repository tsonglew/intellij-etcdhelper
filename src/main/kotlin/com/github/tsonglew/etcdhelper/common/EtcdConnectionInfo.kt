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

import kotlinx.serialization.Serializable
import java.util.*


// use PasswordSafe
//https://plugins.jetbrains.com/docs/intellij/persisting-sensitive-data.html?from=jetbrains.org#retrieve-stored-credentials
@Serializable
data class EtcdConnectionInfo(
    var endpoints: String,
    var username: String,
    var enableAuth: Boolean? = false,
    var id: String? = null,
    var name: String? = null,
    var enableTls: Boolean? = false,
    var tlsCaCert: String? = null,
    var tlsClientKey: String? = null,
    var tlsClientCert: String? = null,
) {
    init {
        if (id == null) {
            id = UUID.randomUUID().toString()
        }
    }

    constructor() : this("", "")

    override fun toString(): String {
        return if (name != null) "$name($username@$endpoints)" else "$username@$endpoints"
    }

    fun update(etcdConnectionInfo: EtcdConnectionInfo): EtcdConnectionInfo {
        endpoints = etcdConnectionInfo.endpoints
        username = etcdConnectionInfo.username
        enableAuth = etcdConnectionInfo.enableAuth
        name = etcdConnectionInfo.name
        enableTls = etcdConnectionInfo.enableTls
        tlsClientCert = etcdConnectionInfo.tlsClientCert
        tlsClientKey = etcdConnectionInfo.tlsClientKey
        tlsCaCert = etcdConnectionInfo.tlsCaCert
        return this
    }
}
