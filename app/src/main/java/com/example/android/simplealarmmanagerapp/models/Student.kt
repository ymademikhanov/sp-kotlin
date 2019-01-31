package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

class Student(var studentId: Int,
              email: String,
              password: String,
              firstname: String?,
              lastname: String?)
    : Account(email, password, firstname, lastname) {

    override fun getJSON() : JSONObject {
        val payload = mapOf(
                "student_id" to studentId,
                "firstname" to firstname,
                "lastname" to lastname,
                "email" to email,
                "password" to password)
        return JSONObject(payload)
    }

    override fun toString(): String {
        return "{" + "studentId: " + studentId +
                "\n firstname: " + firstname +
                "\n lastname: " + lastname  +
                "\n" + super.toString() + " } "
    }
}