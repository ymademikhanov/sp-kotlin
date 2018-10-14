package com.example.android.simplealarmmanagerapp.form_creators

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.android.simplealarmmanagerapp.models.FormField
import com.example.android.simplealarmmanagerapp.models.FormFieldType
import com.example.android.simplealarmmanagerapp.models.Student
import khttp.get

class StudentFormCreator(var context: Context) {
    var TAG: String = "InstructorFormCreator"
    lateinit var layout: LinearLayout
    lateinit var firstnameField: FormField
    lateinit var lastnameField: FormField
    lateinit var emailField: FormField
    lateinit var passwordField: FormField
    lateinit var submitButton: Button

    fun create() {
        layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        firstnameField = FormField(context, FormFieldType.NAME, "Firstname")
        lastnameField = FormField(context, FormFieldType.NAME, "Lastname")
        emailField = FormField(context, FormFieldType.EMAIL, "Email")
        passwordField = FormField(context, FormFieldType.PASSWORD, "Password")

        submitButton = Button(context)
        submitButton.text = "Register"

        submitButton.setOnClickListener(View.OnClickListener {
            Log.d(TAG, getStudent().toString())
        })

        layout.addView(firstnameField.editText)
        layout.addView(lastnameField.editText)
        layout.addView(emailField.editText)
        layout.addView(passwordField.editText)
        layout.addView(submitButton)
    }

    fun getStudent() : Student {
        var student = Student(
                firstnameField.getText(),
                lastnameField.getText(),
                emailField.getText(),
                passwordField.getText()
        )
        return student
    }
}