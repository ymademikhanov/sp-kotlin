package com.example.android.simplealarmmanagerapp.models

class Instructor(var firstname: String, var lastname: String, var office: String, var phone: String, email: String, password: String) : Account(email, password) {
    var instructorId: Int = 1

    override fun toString(): String {
        return "{ firstname: " + firstname + "\n lastname: " + lastname + "\n office: " + office + "\n phone: " + phone + "\n" + super.toString() + " } "
    }
}