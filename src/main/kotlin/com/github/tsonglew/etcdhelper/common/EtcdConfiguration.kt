package com.github.tsonglew.etcdhelper.common

data class EtcdConfiguration(
    val endpoints: String,
    val username: String,
    val password: String
) {
    override fun toString(): String {
        return "EtcdConfiguration(endpoints='$endpoints', username='$username', password='$password')"
    }
}