package com.example.android.simplealarmmanagerapp.utilities

import android.content.SharedPreferences
import com.example.android.simplealarmmanagerapp.models.Account

class LocalAccountManager {
    companion object {
        fun saveAccount(prefs: SharedPreferences, account: Account, jwt: String) {
            prefs.edit {
                put("email" to account.email)
                put("password" to account.password)
                put("id" to account.id)
                put("type" to account.type!!)
                put("signedIn" to true)
                put("jwt" to jwt)
            }
        }

        fun loadAccount(prefs: SharedPreferences) : Account? {
            if (prefs.getBoolean("signedIn", false)) {
                val email = prefs.getString("email", "")
                val password = prefs.getString("password", "")
                val type = prefs.getString("type", "")
                val id = prefs.getInt("id", 0)
                var account = Account(email!!, password!!, null, null)
                account.type = type
                account.id = id
                return account
            }
            return null
        }

        fun deleteAccount(prefs: SharedPreferences) {
            prefs.edit {
                remove("email")
                remove("password")
                remove("id")
                remove("type")
                remove("signedIn")
                remove("jwt")
            }
        }
    }
}