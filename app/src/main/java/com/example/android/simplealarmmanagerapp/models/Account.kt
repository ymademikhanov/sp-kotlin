package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

open class Account {
    var accountId: Int = 1
    var email: String = "mona.rizvi@nu.edu.kz"
    var password: String = "NU is the best!"

    constructor(email: String, password: String) {
        this.email = email
        this.password = password
    }

    override fun toString(): String {
        return "{ accountId: " + accountId + "\n" + "email: " + email + "\n" + "password: " + password + " }";
    }

    open fun getJSON(): JSONObject {
        val payload = mapOf(
                "email" to email,
                "password" to password)
        return JSONObject(payload)
    }
}