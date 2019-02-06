package com.example.android.simplealarmmanagerapp.utilities

import android.content.SharedPreferences
import com.example.android.simplealarmmanagerapp.models.Account

class LocalAccountManager {
    companion object {
        fun saveAccount(preferences: SharedPreferences, account: Account, jwt: String) {
            val editor = preferences.edit()
            editor.putString("email", account.email)
            editor.putString("password", account.password)
            editor.putInt("id", account.id)
            editor.putString("type", account.type)
            editor.putBoolean("signedIn", true)
            editor.putString("jwt", jwt)
            editor.apply()
        }

        fun loadAccount(preferences: SharedPreferences) : Account? {
            if (preferences.getBoolean("signedIn", false)) {
                val email = preferences.getString("email", "")
                val password = preferences.getString("password", "")
                val type = preferences.getString("type", "")
                val id = preferences.getInt("id", 0)
                var account = Account(email!!, password!!, null, null)
                account.type = type
                account.id = id
                return account
            }
            return null
        }

        fun deleteAccount(preferences: SharedPreferences) {
            val editor = preferences.edit()
            editor.remove("email")
            editor.remove("password")
            editor.remove("id")
            editor.remove("type")
            editor.remove("signedIn")
            editor.remove("jwt")
            editor.apply()
        }
    }
}