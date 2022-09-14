package com.github.tsonglew.etcdhelper.common

import kotlinx.serialization.Serializable
import java.io.Serial
import java.util.UUID


// use PasswordSafe
//https://plugins.jetbrains.com/docs/intellij/persisting-sensitive-data.html?from=jetbrains.org#retrieve-stored-credentials
@Serializable
data class EtcdConnectionInfo(
    val endpoints: String,
    val username: String,
    val password: String
) {
    val id: String = UUID.randomUUID().toString()

    constructor() : this("", "", "")

    override fun toString(): String {
        return "$username@$endpoints"
    }
}
