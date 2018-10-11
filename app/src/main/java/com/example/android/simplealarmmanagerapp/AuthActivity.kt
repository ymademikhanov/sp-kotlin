package com.example.android.simplealarmmanagerapp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.ToggleButton
import com.example.android.simplealarmmanagerapp.form_creators.InstructorFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.LoginFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.StudentFormCreator

import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {

    lateinit var instructorFormCreator: InstructorFormCreator
    lateinit var loginFormCreator: LoginFormCreator
    lateinit var studentFormCreator: StudentFormCreator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        instructorFormCreator = InstructorFormCreator(this)
        loginFormCreator = LoginFormCreator(this)

        studentFormCreator = StudentFormCreator(this)
        studentFormCreator.create()

        instructorFormCreator.create()
        loginFormCreator.create()

        auth_form_layout.addView(loginFormCreator.layout)

        val toggle: Switch = findViewById(R.id.auth_switch)

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                auth_form_layout.removeAllViews()
                auth_form_layout.addView(studentFormCreator.layout)

                val accountTypeSwitch = Switch(this)
                accountTypeSwitch.text = "I am instructor"
                accountTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        auth_form_layout.removeAllViews()
                        auth_form_layout.addView(instructorFormCreator.layout)
                        auth_form_layout.addView(accountTypeSwitch)
                    } else {
                        auth_form_layout.removeAllViews()
                        auth_form_layout.addView(studentFormCreator.layout)
                        auth_form_layout.addView(accountTypeSwitch)
                    }
                }
                auth_form_layout.addView(accountTypeSwitch)
            } else {
                auth_form_layout.removeAllViews()
                auth_form_layout.addView(loginFormCreator.layout)
            }
        }
    }
}
