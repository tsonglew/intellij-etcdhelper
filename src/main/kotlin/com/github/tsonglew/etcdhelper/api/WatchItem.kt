package com.github.tsonglew.etcdhelper.api

import io.etcd.jetcd.Watch.Watcher

data class WatchItem(
    val key: String,
    val isPrefix: Boolean,
    val noPut: Boolean,
    val noDelete: Boolean,
    val prevKv: Boolean,
    var watcher: Watcher?
) {
    constructor() : this("", false, false, false, false, null)

    override fun toString() =
        "WatchItem(key='$key', isPrefix=$isPrefix, noPut=$noPut, noDelete=$noDelete, prevKv=$prevKv)"
}
