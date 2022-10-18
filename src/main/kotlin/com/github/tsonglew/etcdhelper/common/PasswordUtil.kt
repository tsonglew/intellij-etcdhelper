package com.github.tsonglew.etcdhelper.common

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.credentialStore.generateServiceName
import com.intellij.ide.passwordSafe.PasswordSafe


object PasswordUtil {

    @JvmStatic
    fun retrievePassword(connectionId: String): String? {
        val credentialAttributes = createCredentialAttributes(connectionId)
        return PasswordSafe.instance.getPassword(credentialAttributes)
    }

    @JvmStatic
    fun savePassword(connectionId: String, password: String) {
        val credentialAttributes = createCredentialAttributes(connectionId)
        val credentials = if (password.isNotEmpty()) Credentials(connectionId, password) else null

        PasswordSafe.instance[credentialAttributes] = credentials
    }

    private fun createCredentialAttributes(connectionId: String): CredentialAttributes {
        return CredentialAttributes(
                generateServiceName("etcdHelper", connectionId)
        )
    }
}
