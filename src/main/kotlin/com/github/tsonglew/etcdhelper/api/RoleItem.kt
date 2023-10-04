package com.github.tsonglew.etcdhelper.api

import io.etcd.jetcd.auth.Permission


data class RoleItem(
    val role: String,
    val permissions: List<Permission>
)
