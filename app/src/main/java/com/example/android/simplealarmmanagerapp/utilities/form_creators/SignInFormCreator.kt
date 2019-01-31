package com.example.android.simplealarmmanagerapp.utilities.form_creators

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.FormField
import com.example.android.simplealarmmanagerapp.models.FormFieldType

class SignInFormCreator(var context: Context, var listener: View.OnClickListener) {
    var TAG: String = "InstructorFormCreator"
    lateinit var layout: LinearLayout
    lateinit var emailField: FormField
    lateinit var passwordField: FormField
    lateinit var submitButton: Button

    fun create() {
        layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        emailField = FormField(context, FormFieldType.EMAIL, "Email")
        passwordField = FormField(context, FormFieldType.PASSWORD, "Password")

        submitButton = Button(context)
        submitButton.text = "Login"
        submitButton.setOnClickListener(listener)

        layout.addView(emailField.editText)
        layout.addView(passwordField.editText)
        layout.addView(submitButton)
    }

    fun fillInAccount(account: Account) {
        emailField.editText.setText(account.email)
        passwordField.editText.setText(account.password)
    }

    fun resetPasswordField() {
        passwordField.editText.setText("")
    }

    fun getAccount() : Account {
        return Account(emailField.getText(), passwordField.getText(), null, null)
    }
}