package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

open class Account(var email: String,
                   var password: String,
                   var firstname: String?,
                   var lastname: String?) {
    var id: Int = 1
    var type: String? = null

    open fun getJSON(): JSONObject {
        val payload = mapOf(
                "email" to email,
                "password" to password)
        return JSONObject(payload)
    }

    override fun toString(): String {
        return "Account{ id: $id, email: $email} "
    }
}