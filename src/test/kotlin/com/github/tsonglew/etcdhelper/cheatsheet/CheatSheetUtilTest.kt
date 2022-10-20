package com.github.tsonglew.etcdhelper.cheatsheet

import junit.framework.TestCase

class CheatSheetUtilTest : TestCase() {

    fun testGetRandomEntry() {
        CheatSheetUtil.getRandomEntry()
                .also { assert(it.first.isNotEmpty()) }
                .also { assert(it.second.isNotEmpty()) }
    }
}
