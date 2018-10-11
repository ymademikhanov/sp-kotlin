package com.example.android.simplealarmmanagerapp.models

class Student : Account {
    var studentId: Int = 1
    var firstname: String = "Anuar"
    var lastname: String = "Otynshin"

    constructor(firstname: String,
                lastname: String,
                email: String,
                password: String) : super(email, password) {
        this.firstname = firstname
        this.lastname = lastname
    }

    override fun toString(): String {
        return "{ firstname: " + firstname + "\n lastname: " + lastname  + "\n" + super.toString() + " } "
    }
}