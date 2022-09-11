package com.github.tsonglew.etcdhelper.common


// use PasswordSafe
//https://plugins.jetbrains.com/docs/intellij/persisting-sensitive-data.html?from=jetbrains.org#retrieve-stored-credentials
data class EtcdConnectionInfo(
    val endpoints: String,
    val username: String,
    val password: String
) {
    constructor() : this("", "", "")
    override fun toString(): String {
        return "Connection(endpoints='$endpoints', username='$username', password='$password')"
    }
}
