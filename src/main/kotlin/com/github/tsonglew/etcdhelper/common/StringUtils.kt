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

import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.jetbrains.annotations.Contract
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.stream.Collectors

/**
 * @author tsonglew
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
object StringUtils {
    private const val SLASH = "/"
    private const val NEWLINE = "\n"
    private const val ESCAPED_NEWLINE = "\\n"
    private const val EMPTY = ""
    private const val UNDERSCORE = "_"
    const val AT = "@"
    private const val HYPHEN = "-"
    const val SPACE = " "
    const val COMMA = ","
    const val SEMICOLON = ";"
    private const val COLON = ":"
    const val DOT = "."
    const val ELLIPSIS = "..."
    const val EMPTY_MAP = "{}"
    const val ZERO = "0"
    private val CHARSET: Charset = StandardCharsets.UTF_8

    fun join(delimiter: String?, vararg parts: String?): String {
        return Arrays.stream(parts)
            .map { p: String? -> p ?: EMPTY }
            .collect(Collectors.joining(delimiter ?: EMPTY))
    }

    @Contract(value = "null -> new", pure = true)
    fun hyphenSplit(s: String?): Array<String?> {
        return s?.split(HYPHEN.toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
            ?: arrayOfNulls(0)
    }

    fun hyphenJoin(vararg parts: String?): String {
        return join(HYPHEN, *parts)
    }

    fun underscoreJoin(vararg parts: String?): String {
        return join(UNDERSCORE, *parts)
    }

    fun slashJoin(vararg parts: String?): String {
        return join(SLASH, *parts)
    }

    @Contract(pure = true)
    fun slashSplit(s: String?): Array<String?> {
        return s?.split(SLASH.toRegex())?.dropLastWhile { it.isEmpty() }
            ?.toTypedArray()
            ?: arrayOfNulls(0)
    }

    @Contract(value = "_ -> new", pure = true)
    fun bytes2String(bytes: ByteArray?): String {
        return String(bytes!!, CHARSET)
    }

    @JvmStatic
    fun string2Bytes(s: String?): ByteArray {
        return s?.toByteArray(CHARSET)
            ?: EMPTY.toByteArray(CHARSET)
    }

    @Contract("null -> new")
    fun newlineSplit(s: Any?): Array<String?> {
        return s?.toString()?.split(NEWLINE.toRegex())
            ?.dropLastWhile { it.isEmpty() }?.toTypedArray()
            ?: arrayOfNulls(0)
    }

    fun escapedNewlineJoin(vararg parts: String?): String {
        return join(ESCAPED_NEWLINE, *parts)
    }

}
