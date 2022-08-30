package com.github.tsonglew.etcdhelper.data

class User private constructor() {
    companion object {
        private var instance : User? = null

        fun getInstance(): User = instance ?: synchronized(this) {
                instance?: User().also { instance = it }
        }
    }
}
