package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.Student
import com.example.android.simplealarmmanagerapp.utilities.auth_network.AuthSubscriber
import com.example.android.simplealarmmanagerapp.utilities.auth_network.SignInPerformer
import com.example.android.simplealarmmanagerapp.utilities.auth_network.SignUpPerformer
import com.example.android.simplealarmmanagerapp.utilities.constants.*
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.forms.sti.progresslitieigb.ProgressLoadingIGB
import com.forms.sti.progresslitieigb.finishLoadingIGB
import com.thejuki.kformmaster.helper.*
import com.thejuki.kformmaster.model.FormEmailEditTextElement
import com.thejuki.kformmaster.model.FormNumberEditTextElement
import com.thejuki.kformmaster.model.FormPasswordEditTextElement
import com.thejuki.kformmaster.model.FormSingleLineEditTextElement
import com.thejuki.kformmaster.state.FormEditTextViewState
import kotlinx.android.synthetic.main.activity_sign_up_student.*

class StudentSignUpActivity : AppCompatActivity(), AuthSubscriber {

    lateinit var context: Context
    lateinit var signUpForm: FormBuildHelper
    lateinit var preferences: SharedPreferences

    var account: Account? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_student)

        // Initialize variables.
        context = this
        preferences = context.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        // Initializing UI.
        initUI()
    }

    private enum class Tag {
        Email,
        Password,
        First,
        Last,
        StudentID
    }

    private fun initUI() {
        // Initialize sign-up form.

        signUpForm = form(context, findViewById(R.id.signUpRecyclerView)) {
            email(Tag.Email.ordinal) {
                title = getString(R.string.email)
                required = true
            }
            password(Tag.Password.ordinal) {
                title = getString(R.string.password)
                required = true
            }
            text(Tag.First.ordinal) {
                title = getString(R.string.firstname)
                required = true
            }
            text(Tag.Last.ordinal) {
                title = getString(R.string.lastname)
                required = true
            }
            number(Tag.StudentID.ordinal) {
                title = "Student ID"
                required = true
                numbersOnly = true
            }
            button(1) {
                value = "Sign up"
                backgroundColor = PRIMARY_COLOR

                valueObservers.add { _, _ ->
                    // Performing sign-in.
                    if (signUpForm.isValidForm) {
                        val email = signUpForm
                                .getFormElement<FormEmailEditTextElement>(Tag.Email.ordinal)
                                .value
                        val password = signUpForm
                                .getFormElement<FormPasswordEditTextElement>(Tag.Password.ordinal)
                                .value
                        val firstname = signUpForm
                                .getFormElement<FormSingleLineEditTextElement>(Tag.First.ordinal)
                                .value
                        val lastname = signUpForm
                                .getFormElement<FormSingleLineEditTextElement>(Tag.Last.ordinal)
                                .value
                        val studentID = signUpForm
                                .getFormElement<FormNumberEditTextElement>(Tag.StudentID.ordinal)
                                .value.toString().toInt()

                        val student = Student(studentID, email!!, password!!, firstname, lastname)
                        performSignUp(student)
                    }
                }
            }
        }

        // Setting behavior on click on switch to sign in button.
        switch_to_sign_in.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val signInActivityIntent = Intent(context, SignInActivity::class.java)
                signInActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(signInActivityIntent)
                finish()
            }
        })
    }

    fun performSignUp(account: Account) {
        // Starting loading dialog.
        ProgressLoadingIGB.startLoadingIGB(context) {
            message = "Signing up!"
            srcLottieJson = R.raw.progress_animation
            timer = 1000000
        }

        // Running sign-in performer.
        val signUpPerformer = SignUpPerformer(this, STUDENT_URL)
        signUpPerformer.execute(account)
    }

    override fun handleAuthUpdate(pair: Pair<Resource<Account>, String?>) {
        val resource = pair.first
        val jwt = pair.second

        when(resource.status) {
            Resource.Status.SUCCESS -> {
                finishLoadingIGB()
                saveAccountToDevice(resource.data!!, jwt!!)
                val signInActivityIntent = Intent(context, SignInActivity::class.java)
                signInActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                startActivity(signInActivityIntent)
                finish()
            }
            Resource.Status.ERROR -> {
                finishLoadingIGB()
                ProgressLoadingIGB.startLoadingIGB(context) {
                    message = pair.first.message!!
                    srcLottieJson = R.raw.loading_error
                    timer = 2000
                }
            }
        }
    }

    private fun saveAccountToDevice(account: Account, jwt: String) {
        val editor = preferences.edit()
        editor.putString("email", account.email)
        editor.putString("password", account.password)
        editor.putInt("id", account.id)
        editor.putString("type", account.type)
        editor.putBoolean("signedIn", true)
        editor.putString("jwt", jwt)
        editor.apply()
    }
}