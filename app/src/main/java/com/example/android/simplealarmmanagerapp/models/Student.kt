package com.example.android.simplealarmmanagerapp.models

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
}