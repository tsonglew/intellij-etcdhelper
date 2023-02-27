package com.github.tsonglew.etcdhelper.api

data class WatchItem(
    val key: String,
    val isPrefix: Boolean,
    val noPut: Boolean,
    val noDelete: Boolean,
    val prevKv: Boolean
)
