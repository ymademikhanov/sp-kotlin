package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.models.daos.AppDatabase
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDaoLocal
import com.example.android.simplealarmmanagerapp.models.daos.AttendanceCheckReportDao
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepository
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepositoryImpl
import com.example.android.simplealarmmanagerapp.utilities.LocalAccountManager
import com.example.android.simplealarmmanagerapp.utilities.auth_network.AuthSubscriber
import com.example.android.simplealarmmanagerapp.utilities.auth_network.SignInPerformer
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_COLOR
import com.example.android.simplealarmmanagerapp.utilities.constants.SIGN_IN_URL
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.forms.sti.progresslitieigb.ProgressLoadingIGB
import com.forms.sti.progresslitieigb.finishLoadingIGB
import com.thejuki.kformmaster.helper.*
import com.thejuki.kformmaster.model.FormEmailEditTextElement
import com.thejuki.kformmaster.model.FormPasswordEditTextElement
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_signin.*

class SignInActivity : AppCompatActivity(), AuthSubscriber {
    val TAG = "SignInActivity"

    lateinit var context: Context
    lateinit var signInForm: FormBuildHelper
    lateinit var preferences: SharedPreferences
    var account: Account? = null

    private var db: AppDatabase? = null
    private var reportDao: AttendanceCheckReportDao? = null

    lateinit var attCheckReportRepository: AttCheckReportRepository

    private enum class Tag {
        Email,
        Password,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize variables.
        context = this
        preferences = context.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)
        account = LocalAccountManager.loadAccount(preferences)

        setContentView(R.layout.activity_signin)

        // Initializing UI.
        initUI()

        // Auto signing-in.
        autoSign()
    }

    private fun initUI() {
        // Initializing sign-in form.
        signInForm = form(context, findViewById(R.id.signInRecyclerView)) {
            email(Tag.Email.ordinal) {
                title = getString(R.string.email)
                value = account?.email
                required = true
            }
            password(Tag.Password.ordinal) {
                title = getString(R.string.password)
                value = account?.password
                required = true
            }
            button(1) {
                value = "Sign in"
                backgroundColor = PRIMARY_COLOR

                valueObservers.add { _, _ ->
                    // Performing sign-in.
                    if (signInForm.isValidForm) {
                        val email = signInForm
                                .getFormElement<FormEmailEditTextElement>(Tag.Email.ordinal)
                                .value
                        val password = signInForm
                                .getFormElement<FormPasswordEditTextElement>(Tag.Password.ordinal)
                                .value
                        val account = Account(email!!, password!!, null, null)
                        performSignIn(account)
                    }
                }
            }
        }

        // Setting behavior on click on switch to sign up button.
        switch_to_student_sign_up.setOnClickListener {
            val signUpActivityIntent = Intent(context, StudentSignUpActivity::class.java)
            startActivity(signUpActivityIntent)
            finish()
        }
    }

    fun autoSign() {
        // If account already signed in before.
        if (account != null) {
            performSignIn(account!!)
        }
    }

    fun performSignIn(account: Account) {
        // Starting loading dialog.
        ProgressLoadingIGB.startLoadingIGB(context) {
            message = "Signing in!"
            srcLottieJson = R.raw.progress_animation
            timer = 1000000
        }

        // Running sign-in performer.
        val signInPerformer = SignInPerformer(this, SIGN_IN_URL)
        signInPerformer.execute(account)
    }

    override fun handleAuthUpdate(pair: Pair<Resource<Account>, String?>) {
        val resource = pair.first
        val jwt = pair.second

        when(resource.status) {
            Resource.Status.SUCCESS -> {
                finishLoadingIGB()
                ProgressLoadingIGB.startLoadingIGB(context) {
                    message = "Success!"
                    srcLottieJson = R.raw.loading_success
                    timer = 1000
                }

                Handler().postDelayed({
                    finishLoadingIGB()
                    LocalAccountManager.saveAccount(preferences, resource.data!!, jwt!!)
                    val homeActivityIntent = Intent(context, MainActivity::class.java)
                    startActivity(homeActivityIntent)
                }, 1000)
            }
            Resource.Status.ERROR -> {
                finishLoadingIGB()
                ProgressLoadingIGB.startLoadingIGB(context) {
                    message = pair.first.message!!
                    srcLottieJson = R.raw.error_animation
                    timer = 2000
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        finishLoadingIGB()
    }
}