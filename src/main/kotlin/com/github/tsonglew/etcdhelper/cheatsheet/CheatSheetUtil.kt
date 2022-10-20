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
