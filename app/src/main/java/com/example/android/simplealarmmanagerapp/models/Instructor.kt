package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

class Instructor(email: String,
                 password: String,
                 firstname: String?,
                 lastname: String?,
                 var office: String,
                 var phone: String)
    : Account(email, password, firstname, lastname) {

    var instructorId: Int = 1

    override fun getJSON(): JSONObject {
        val payload = mapOf(
                "first_name" to firstname,
                "last_name" to lastname,
                "email" to email,
                "password" to password)
        return JSONObject(payload)
    }

    override fun toString(): String {
        return "{ firstname: " + firstname +
                "\n lastname: " + lastname +
                "\n office: " + office +
                "\n phone: " + phone +
                "\n" + super.toString() + " } "
    }
}