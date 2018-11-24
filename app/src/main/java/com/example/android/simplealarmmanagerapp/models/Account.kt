package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

open class Account(var email: String, var password: String) {
    var accountId: Int = 1

    open fun getJSON(): JSONObject {
        val payload = mapOf(
                "email" to email,
                "password" to password)
        return JSONObject(payload)
    }

    override fun toString(): String {
        return "{ accountId: $accountId\nemail: $email\npassword: $password }";
    }
}