package com.example.android.simplealarmmanagerapp.form_creators

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.android.simplealarmmanagerapp.models.FormField
import com.example.android.simplealarmmanagerapp.models.FormFieldType
import com.example.android.simplealarmmanagerapp.models.Instructor

class InstructorSignUpFormCreator(var context: Context) {
    var TAG: String = "InstructorFormCreator"
    lateinit var layout: LinearLayout
    lateinit var firstnameField: FormField
    lateinit var lastnameField: FormField
    lateinit var officeField: FormField
    lateinit var phoneField: FormField
    lateinit var emailField: FormField
    lateinit var passwordField: FormField
    lateinit var submitButton: Button

    fun create() {
        layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        firstnameField = FormField(context, FormFieldType.NAME, "Firstname")
        lastnameField = FormField(context, FormFieldType.NAME, "Lastname")
        officeField = FormField(context, FormFieldType.TEXT, "Office")
        phoneField = FormField(context, FormFieldType.TEXT, "Phone")
        emailField = FormField(context, FormFieldType.EMAIL, "Email")
        passwordField = FormField(context, FormFieldType.PASSWORD, "Password")

        submitButton = Button(context)
        submitButton.text = "Register"
        submitButton.setOnClickListener(View.OnClickListener {
            Log.d(TAG, getInstructor().toString())
        })

        layout.addView(firstnameField.editText)
        layout.addView(lastnameField.editText)
        layout.addView(officeField.editText)
        layout.addView(phoneField.editText)
        layout.addView(emailField.editText)
        layout.addView(passwordField.editText)
        layout.addView(submitButton)
    }

    fun resetPasswordField() {
        passwordField.editText.setText("")
    }

    fun getInstructor() : Instructor {
        var instructor = Instructor(
                firstnameField.getText(),
                lastnameField.getText(),
                officeField.getText(),
                phoneField.getText(),
                emailField.getText(),
                passwordField.getText()
                )
        return instructor
    }
}