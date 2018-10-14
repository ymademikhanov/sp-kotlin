package com.example.android.simplealarmmanagerapp

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import com.example.android.simplealarmmanagerapp.form_creators.InstructorFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.LoginFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.StudentFormCreator
import com.example.android.simplealarmmanagerapp.models.Account
import khttp.post
import khttp.responses.Response

import kotlinx.android.synthetic.main.activity_auth.*
import org.json.JSONObject
import java.util.*

class AuthActivity : AppCompatActivity(), View.OnClickListener {

    val TAG = "AuthActivity"

    lateinit var instructorFormCreator: InstructorFormCreator
    lateinit var loginFormCreator: LoginFormCreator
    lateinit var studentFormCreator: StudentFormCreator
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        context = this

        loginFormCreator = LoginFormCreator(this)
        studentFormCreator = StudentFormCreator(this)
        instructorFormCreator = InstructorFormCreator(this)

        loginFormCreator.create()
        studentFormCreator.create()
        instructorFormCreator.create()

        loginFormCreator.submitButton.setOnClickListener(this)

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

    override fun onClick(v: View?) {
        LoginPerformerInBackground().execute(loginFormCreator.getAccount())
    }

    inner class LoginPerformerInBackground: AsyncTask<Account, String, Response>() {

        override fun doInBackground(vararg accounts: Account?): Response {
            val account = accounts[0]

            val payload = mapOf("email" to account?.email, "password" to account?.password)
            var r = post("https://attendance-app-dev.herokuapp.com/api/v1/auth/signin", data= JSONObject(payload))

            return r
        }

        override fun onPostExecute(r: Response) {
            if (r.statusCode == 200 && r.text == "\"\"") {
                val navigateToMainPage = Intent(context, MainActivity::class.java)
                navigateToMainPage.putExtra("JWToken", r.headers.get("X-Auth").toString())
                startActivity(navigateToMainPage)
            }
        }
    }

    fun ShowLog(message: String) {
        Log.w(TAG, Date().toString() + " : " + message)
    }
}
