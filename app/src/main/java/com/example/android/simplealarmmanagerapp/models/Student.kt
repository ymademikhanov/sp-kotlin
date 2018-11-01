package com.example.android.simplealarmmanagerapp.models

import org.json.JSONObject

class Student : Account {
    var studentId: Int = 1
    var firstname: String = "Anuar"
    var lastname: String = "Otynshin"

    constructor(studentId: Int,
                firstname: String,
                lastname: String,
                email: String,
                password: String) : super(email, password) {
        this.studentId = studentId
        this.firstname = firstname
        this.lastname = lastname
    }

    override fun toString(): String {
        return "{" + "studentId: " + studentId + "\n firstname: " + firstname + "\n lastname: " + lastname  + "\n" + super.toString() + " } "
    }

    override fun getJSON() : JSONObject {
        val payload = mapOf(
                "student_id" to studentId,
                "firstname" to firstname,
                "lastname" to lastname,
                "email" to email,
                "password" to password)

        return JSONObject(payload)
    }
}