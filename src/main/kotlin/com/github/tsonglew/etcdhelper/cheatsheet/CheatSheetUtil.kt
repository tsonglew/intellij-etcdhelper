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

package com.github.tsonglew.etcdhelper.cheatsheet

object CheatSheetUtil {
    private val cheatSheet = mapOf(
            "Cluster infos" to mapOf("Details of a host" to "etcdctl get /_etc/machines/<token> "),
            "Accessing the key space" to mapOf(
                    "get top-level keys" to "etcdctl get / --prefix --keys-only",
                    "get top-level keys and values" to "etcdctl get / --prefix",
                    "get full tree" to "etcdctl get / --prefix --keys-only --recursive",
                    "get key details" to "etcdctl get <key path>",
                    "get key value only" to "etcdctl get <key path> --print-value-only",
                    "get older revision of a key" to "etcdctl get <key path> --rev=<number>",
                    "get key and metadata" to "etcdctl -o extended get <key path>",
                    "Output in JSON with metadata" to "etcdctl get <key path> -w=json"
            ),
            "Batch queries" to mapOf(
                    "get all keys key1, key2, key3, ..., key10" to "etcdctl get key1 key10",
                    "get all keys matching ^key" to "etcdctl get --prefix key",
                    "get max 10 keys matching ^key" to "etcdctl get --prefix key --limit=10"
            ),
            "Creating a path" to mapOf(
                    "Removes only empty paths" to "etcdctl rmdir /newpath"
            ),
            "Manipulate keys" to mapOf(
                    "create a key" to "etcdctl mk /path/newkey some-data",
                    "create key or update it" to "etcdctl set /path/newkey some-data",
                    "update existing key" to "etcdctl update /path/newkey new-data"
            )
    )

    @JvmStatic
    fun getRandomEntry() = cheatSheet.toList().random().second.toList().random()
}
