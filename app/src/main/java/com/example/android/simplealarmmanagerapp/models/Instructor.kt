package com.example.android.simplealarmmanagerapp.models

class Instructor : Account {
    var instructorId: Int = 1
    var firstname: String = "Mona"
    var lastname: String = "Rizvi"
    var office: String = "7.211"
    var phone: String = "+7(777)777-77-77"

    constructor(firstname: String,
                lastname: String,
                office: String,
                phone: String,
                email: String,
                password: String) : super(email, password) {
        this.firstname = firstname
        this.lastname = lastname
        this.office = office
        this.phone = phone
    }

    override fun toString(): String {
        return "{ firstname: " + firstname + "\n lastname: " + lastname + "\n office: " + office + "\n phone: " + phone + "\n" + super.toString() + " } "
    }
}