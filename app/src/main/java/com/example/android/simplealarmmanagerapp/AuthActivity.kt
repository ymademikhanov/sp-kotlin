package com.example.android.simplealarmmanagerapp

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import com.example.android.simplealarmmanagerapp.form_creators.InstructorSignUpFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.SignInFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.StudentSignUpFormCreator
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.Student
import io.jsonwebtoken.*
import io.jsonwebtoken.impl.crypto.MacProvider
import khttp.post
import khttp.responses.Response

import kotlinx.android.synthetic.main.activity_auth.*
import org.json.JSONObject
import java.util.*

enum class AuthenticationMode {
    SIGNIN, STUDENT_REGISTRATION, INSTRUCTOR_REGISTRATION
}

class AuthActivity : AppCompatActivity(), View.OnClickListener {

    val signatureAlgorithm = SignatureAlgorithm.HS256

    val SIGNIN_URL = "https://attendance-app-dev.herokuapp.com/api/v1/auth/signin"
    val STUDENT_URL = "https://attendance-app-dev.herokuapp.com/api/v1/students"
    val INSTRUCTOR_URL = "https://attendance-app-dev.herokuapp.com/api/v1/instructors"

    val TAG = "AuthActivity"

    lateinit var instructorSignUpFormCreator: InstructorSignUpFormCreator
    lateinit var signInFormCreator: SignInFormCreator
    lateinit var studentSignUpFormCreator: StudentSignUpFormCreator
    lateinit var context: Context

    lateinit var authenticationMode: AuthenticationMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        context = this

        signInFormCreator = SignInFormCreator(this)
        studentSignUpFormCreator = StudentSignUpFormCreator(this)
        instructorSignUpFormCreator = InstructorSignUpFormCreator(this)

        signInFormCreator.create()
        studentSignUpFormCreator.create()
        instructorSignUpFormCreator.create()

        signInFormCreator.submitButton.setOnClickListener(this)
        studentSignUpFormCreator.submitButton.setOnClickListener(this)
        instructorSignUpFormCreator.submitButton.setOnClickListener(this)


        auth_form_layout.addView(signInFormCreator.layout)
        authenticationMode = AuthenticationMode.SIGNIN

        val toggle: Switch = findViewById(R.id.auth_switch)

        toggle.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                auth_form_layout.removeAllViews()
                auth_form_layout.addView(studentSignUpFormCreator.layout)
                this.authenticationMode = AuthenticationMode.STUDENT_REGISTRATION

                val accountTypeSwitch = Switch(this)
                accountTypeSwitch.text = "I am instructor"
                accountTypeSwitch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        auth_form_layout.removeAllViews()
                        auth_form_layout.addView(instructorSignUpFormCreator.layout)
                        auth_form_layout.addView(accountTypeSwitch)
                        this.authenticationMode = AuthenticationMode.INSTRUCTOR_REGISTRATION
                    } else {
                        auth_form_layout.removeAllViews()
                        auth_form_layout.addView(studentSignUpFormCreator.layout)
                        auth_form_layout.addView(accountTypeSwitch)
                        this.authenticationMode = AuthenticationMode.STUDENT_REGISTRATION
                    }
                }
                auth_form_layout.addView(accountTypeSwitch)
            } else {
                auth_form_layout.removeAllViews()
                auth_form_layout.addView(signInFormCreator.layout)
                this.authenticationMode = AuthenticationMode.SIGNIN
            }
        }
    }

    override fun onClick(v: View?) {
        when (authenticationMode) {
            AuthenticationMode.SIGNIN -> {
                ShowLog("Signing is started.")
                SignInPerformerInBackground().execute(signInFormCreator.getAccount())
                ShowLog("Signing is ended.")
            }
            AuthenticationMode.STUDENT_REGISTRATION -> {
                ShowLog("Student sign up is started.")
                StudentSignUpPerformerInBackground().execute(studentSignUpFormCreator.getStudent())
                ShowLog("Student sign up is ended.")
            }
            AuthenticationMode.INSTRUCTOR_REGISTRATION -> {
                ShowLog("Instructor sign up DO NOTHING FOR NOW.")
                // do nothing for now.
            }
        }

    }

    inner class SignInPerformerInBackground: AsyncTask<Account, String, Response>() {
        override fun doInBackground(vararg accounts: Account?): Response {
            val account = accounts[0]

            val payload = mapOf(
                    "email" to account?.email,
                    "password" to account?.password)

            ShowLog("Signin payload: " + payload)
            return post(SIGNIN_URL, data= JSONObject(payload))
        }

        override fun onPostExecute(r: Response) {
            if (r.statusCode == 200 && r.text == "\"\"") {
                ShowLog("Successful sign in.")
                val navigateToMainPage = Intent(context, CourseListActivity::class.java)
                val jwtToken = r.headers.get("X-Auth").toString()

//                val key = MacProvider.generateKey(signatureAlgorithm, "SECRET")
                navigateToMainPage.putExtra("JWToken", jwtToken)



                val split_string = jwtToken.split(".")

                val base64EncodedHeader = split_string[0]
                val base64EncodedBody = split_string[1]
                val base64EncodedSignature = split_string[2]

                println("~~~~~~~~~ JWT Header ~~~~~~~")
//                val header = Base64.getDecoder().decode(base64EncodedHeader)
                val header = android.util.Base64.decode(base64EncodedHeader, android.util.Base64.DEFAULT);
                println("JWT Header : $header")


                println("~~~~~~~~~ JWT Body ~~~~~~~")
//                val body = Base64.getDecoder().decode(base64EncodedBody)
                val body = android.util.Base64.decode(base64EncodedBody, android.util.Base64.DEFAULT);

                val bodyStr = String(body)
                println("JWT Body : $bodyStr")

                startActivity(navigateToMainPage)
            }
        }
    }

    inner class StudentSignUpPerformerInBackground: AsyncTask<Student, String, Response>() {
        override fun doInBackground(vararg students: Student?): Response {
            val student = students[0]
            val payload = mapOf(
                    "student_id" to student?.studentId,
                    "firstname" to student?.firstname,
                    "lastname" to student?.lastname,
                    "email" to student?.email,
                    "password" to student?.password)

            ShowLog("Student signup payload: " + payload)
            return post(STUDENT_URL, data= JSONObject(payload))
        }

        override fun onPostExecute(r: Response) {
//            ShowLog("statusCode: " + r.statusCode + "text: " + r.text)
            if (r.statusCode == 201) {
                ShowLog("Successful student sign up.")
                val navigateToMainPage = Intent(context, MainActivity::class.java)
                navigateToMainPage.putExtra("JWToken", r.headers.get("X-Auth").toString())
                startActivity(navigateToMainPage)
            }
        }
    }

    fun ShowLog(message: String) {
        Log.d(TAG, Date().toString() + " : " + message)
    }
}
