package com.example.android.simplealarmmanagerapp.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.form_creators.InstructorSignUpFormCreator
import com.example.android.simplealarmmanagerapp.utilities.form_creators.SignInFormCreator
import com.example.android.simplealarmmanagerapp.utilities.form_creators.StudentSignUpFormCreator
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.AuthenticationMode
import com.example.android.simplealarmmanagerapp.models.Student
import com.example.android.simplealarmmanagerapp.utilities.JwtToJson
import com.example.android.simplealarmmanagerapp.utilities.auth_network.AuthSubscriber
import com.example.android.simplealarmmanagerapp.utilities.auth_network.SignInPerformer
import com.example.android.simplealarmmanagerapp.utilities.auth_network.SignUpPerformer
import com.example.android.simplealarmmanagerapp.utilities.constants.*
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.example.android.simplealarmmanagerapp.utilities.preferences.removeUser
import khttp.post
import khttp.responses.Response

import kotlinx.android.synthetic.main.activity_auth.*


class AuthActivity : AppCompatActivity(), View.OnClickListener, AuthSubscriber {
    val TAG = "AuthActivity"

    private var authenticationMode: AuthenticationMode = AuthenticationMode.SIGNIN

    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var instructorSignUpFormCreator: InstructorSignUpFormCreator
    lateinit var signInFormCreator: SignInFormCreator
    lateinit var studentSignUpFormCreator: StudentSignUpFormCreator
    lateinit var progressDialog: ProgressDialog
    private lateinit var toggleSwitch: Switch
    private lateinit var signInPerformer: SignInPerformer
    private lateinit var studentSignUpPerformer: SignUpPerformer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        context = this

        // Initializing UI.
        initialiseUI()

        // Initializing auth performers.
        signInPerformer = SignInPerformer(this, SIGN_IN_URL)
        studentSignUpPerformer = SignUpPerformer(this, STUDENT_URL)

        preferences = context.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        if (preferences.getBoolean("signedIn", false)) {
            val email = preferences.getString("email", "")
            val password = preferences.getString("password", "")
            val account = Account(email!!, password!!, null, null)
            signInFormCreator.fillInAccount(account)
            performSignInWithProgressDialog(account)
        }
    }

    fun initialiseUI() {
        // Logging.
        Log.i(TAG, "Initializing UI")

        progressDialog = ProgressDialog(context)

        signInFormCreator = SignInFormCreator(context, this)
        studentSignUpFormCreator = StudentSignUpFormCreator(context, this)
        instructorSignUpFormCreator = InstructorSignUpFormCreator(context, this)

        signInFormCreator.create()
        studentSignUpFormCreator.create()
        instructorSignUpFormCreator.create()

        auth_form_layout.addView(signInFormCreator.layout)

        // Sign-in/Sign-up toggling.
        toggleSwitch = findViewById(R.id.auth_switch)
        toggleSwitch.setOnCheckedChangeListener { _, isChecked ->
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

    override fun handleAuthUpdate(pair: Pair<Resource<Account>, String?>) {
        val resource = pair.first
        val jwt = pair.second

        when(resource.status) {
            Resource.Status.SUCCESS -> {
                // Logging.
                Log.i(TAG, "Successful sign-in or up")

                progressDialog.dismiss()

                // Saving account locally.
                saveAccountToDevice(resource.data!!, jwt!!)

                val navigateToMainPage = Intent(context, HomeActivity::class.java)
                startActivity(navigateToMainPage)
            }
            Resource.Status.ERROR -> {
                // Logging.
                Log.i(TAG, "Failed account sign-in or up. ${resource.message}")

                progressDialog.dismiss()
                Toast.makeText(context, resource.message, Toast.LENGTH_SHORT).show()
            }
            Resource.Status.LOADING -> {
                Log.i(TAG, "Still loading...")
            }
        }
    }

    private fun startProgressDialog(dialogMessage: String) {
        Log.i(TAG, dialogMessage)
        progressDialog.setMessage(dialogMessage)
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    private fun performSignInWithProgressDialog(account: Account) {
        if (verifyAvailableNetwork()) {
            // Starting dialog message.
            startProgressDialog(PROGRESS_DIALOG_SIGN_IN_MESSAGE)
            // Performing sign-in action.
            signInPerformer.execute(account)
        } else {
            Toast.makeText(context, TOAST_NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSignUpWithProgressDialog(student: Student) {
        if (verifyAvailableNetwork()) {
            // Starting dialog message.
            startProgressDialog(PROGRESS_DIALOG_SIGN_UP_MESSAGE)
            // Performing sign-up action.
            studentSignUpPerformer.execute(student)
        } else {
            Toast.makeText(context, TOAST_NO_INTERNET_MESSAGE, Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyAvailableNetwork() : Boolean {
        val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun saveAccountToDevice(account: Account, jwt: String) {
        val editor = preferences.edit()
        editor.putString("email", account.email)
        editor.putString("password", account.password)
        editor.putInt("id", account.id)
        editor.putString("type", account.type)
        editor.putBoolean("signedIn", true)
        editor.putString("jwt", jwt)
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
