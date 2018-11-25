package com.example.android.simplealarmmanagerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.android.simplealarmmanagerapp.constants.ATTENDANCE_CHECK_ALARM_PREFIX
import com.example.android.simplealarmmanagerapp.constants.WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX
import com.example.android.simplealarmmanagerapp.form_creators.InstructorSignUpFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.SignInFormCreator
import com.example.android.simplealarmmanagerapp.form_creators.StudentSignUpFormCreator
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.Student
import khronos.*
import khttp.post
import khttp.responses.Response

import kotlinx.android.synthetic.main.activity_auth.*
import java.time.LocalDateTime

enum class AuthenticationMode {
    SIGNIN, STUDENT_REGISTRATION, INSTRUCTOR_REGISTRATION
}

class AuthActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "AuthActivity"
    val SIGNIN_URL = "https://attendance-app-dev.herokuapp.com/api/v1/auth/signin"
    val STUDENT_URL = "https://attendance-app-dev.herokuapp.com/api/v1/students"

    val PREFERENCES_NAME = "AuthenticationPreferences"

    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var instructorSignUpFormCreator: InstructorSignUpFormCreator
    lateinit var signInFormCreator: SignInFormCreator
    lateinit var studentSignUpFormCreator: StudentSignUpFormCreator
    lateinit var authenticationMode: AuthenticationMode
    lateinit var progressDialog: ProgressDialog
    lateinit var alarmManager : AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        context = this
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        progressDialog = ProgressDialog(context)

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

        if (preferences.getBoolean("signedIn", false)) {
            val email = preferences.getString("email", "")
            val password = preferences.getString("password", "")
            val account = Account(email, password)
            signInFormCreator.fillInAccount(account)
            performSignInWithProgressDialog(account)
        }

        // Sign-in/Sign-up toggling.
        val toggle: Switch = findViewById(R.id.auth_switch)
        authenticationMode = AuthenticationMode.SIGNIN
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
                performSignInWithProgressDialog(signInFormCreator.getAccount())
            }
            AuthenticationMode.STUDENT_REGISTRATION -> {
                performSignUpWithProgressDialog(studentSignUpFormCreator.getStudent())
            }
            AuthenticationMode.INSTRUCTOR_REGISTRATION -> {
            }
        }
    }

    private fun performSignInWithProgressDialog(account: Account) {
        if (verifyAvailableNetwork()) {
            Log.i(TAG, "Sign-in started.")
            progressDialog.setMessage("Signing in...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            SignInPerformerInBackground().execute(account)
        } else {
            Toast.makeText(context, "No internet connectivity =(", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSignUpWithProgressDialog(student: Student) {
        if (verifyAvailableNetwork()) {
            Log.i(TAG, "Student sign-up started")
            progressDialog = ProgressDialog(context)
            progressDialog.setMessage("Signing up...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            StudentSignUpPerformerInBackground().execute(student)
        } else {
            Toast.makeText(context, "No internet connectivity =(", Toast.LENGTH_SHORT).show()
        }
    }

    inner class SignInPerformerInBackground: AsyncTask<Account, String, Response>() {
        override fun doInBackground(vararg accounts: Account?): Response {
            val account = accounts[0]
            val data = account?.getJSON()
            Log.i(TAG, "Sign-in account data: $data")
            saveUserToDevice(account?.email, account?.password)
            val response = post(SIGNIN_URL, data= data)
            Log.i(TAG, "Response: $response")
            return response
        }

        override fun onPostExecute(r: Response) {
            if (r.statusCode == 200 && r.text == "\"\"") {
                Log.i(TAG, "Successful sign-in")
                val jwtToken = r.headers["X-Auth"].toString()
                extractAccountTypeAndIDFromJWT(jwtToken)

                scheduleWeeklyLoadingOfAttendanceChecks()

                val navigateToMainPage = Intent(context, HomeActivity::class.java)
                startActivity(navigateToMainPage)
            } else {
                val parser = Parser()
                val stringBuilder = StringBuilder(r.text)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                val errorMessage = json["message"].toString()
                Log.i(TAG, "Failed account sign in. $errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                signInFormCreator.resetPasswordField()
                removeUserFromDevice()
            }
            progressDialog.dismiss()
        }
    }

    fun scheduleWeeklyLoadingOfAttendanceChecks() {
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.i(TAG, "Setting weekly attendance checks scheduling...")
        val accountType = preferences.getString("accountType", "")
        if (accountType == "student") {
            val accountId = preferences.getInt("accountId", 0)

            // SETTING REPEATED ATTENDANCE CHECK LOADING ON EVERY SUNDAY.
            val today = Dates.today
            var thisSunday = today.with(weekday = 1)
            thisSunday = thisSunday.beginningOfHour.beginningOfMinute

            for (i in 0..15) {
                val intent = Intent(context, WeeklySchedulerOfBTChecks::class.java)
                val alarmId = "$WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX:$accountId:$i"
                val alarmIdHashCode = alarmId.hashCode()
                val pendingIntent = PendingIntent.getBroadcast(context, alarmIdHashCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                alarmManager.setExact(AlarmManager.RTC, thisSunday.time, pendingIntent)
                Log.i(TAG, "Scheduled an attendance checks loading on $thisSunday")
                thisSunday += 1.week
            }
        }
    }

    fun verifyAvailableNetwork() : Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    inner class StudentSignUpPerformerInBackground: AsyncTask<Student, String, Response>() {
        override fun doInBackground(vararg students: Student?): Response {
            val student = students[0]
            val data = student?.getJSON()
            Log.i(TAG, "Sign-up student data: $data")
            saveUserToDevice(student?.email, student?.password)
            val response = post(STUDENT_URL, data=data)
            Log.i(TAG, "Student sign-up. Response: $response")
            return response
        }

        override fun onPostExecute(r: Response) {
            if (r.statusCode == 201) {
                Log.i(TAG, "Successful student sign up.")
                val jwt = r.headers["X-Auth"].toString()
                extractAccountTypeAndIDFromJWT(jwt)

                scheduleWeeklyLoadingOfAttendanceChecks()

                val navigateToMainPage = Intent(context, CourseListActivity::class.java)
                startActivity(navigateToMainPage)
            } else {
                val parser = Parser()
                val stringBuilder = StringBuilder(r.text)
                val json: JsonObject = parser.parse(stringBuilder) as JsonObject
                val errorMessage = json["message"].toString()
                Log.i(TAG, "Failed student sign up: $errorMessage")
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                studentSignUpFormCreator.resetPasswordField()
                removeUserFromDevice()
            }
            progressDialog.dismiss()
        }
    }

    fun extractAccountTypeAndIDFromJWT(jwt: String) {
        val splitString = jwt.split(".")
        val base64EncodedBody = splitString[1]
        val body = android.util.Base64.decode(base64EncodedBody, android.util.Base64.DEFAULT);
        val bodyStr = String(body)
        val parser = Parser()
        val stringBuilder = StringBuilder(bodyStr)
        val json: JsonObject = parser.parse(stringBuilder) as JsonObject

        val editor = preferences.edit()
        val accountType = json.string("account_type")
        val accountId = json.int("account_id")

        editor.putString("jwt", jwt)
        editor.putString("accountType", accountType)
        editor.putInt("accountId", accountId!!)

        editor.apply()
        Log.i(TAG, "Account type: $accountType and Id: $accountId")
    }

    fun saveUserToDevice(email: String?, password: String?) {
        val editor = preferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("signedIn", true)
        editor.apply()
    }

    fun removeUserFromDevice() {
        val editor = preferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("signedIn")
        editor.apply()
    }

    override fun onPause() {
        super.onPause()
        progressDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }
}
