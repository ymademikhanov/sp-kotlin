package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_COLOR
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_DARK_COLOR
import com.thejuki.kformmaster.helper.*

class SignInActivity : AppCompatActivity() {

    lateinit var context: Context

    private enum class Tag {
        Email,
        Password,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_signin)

        // Initializing UI.
        initUI()
    }

    private fun initUI() {
        // Initialize variables
        val formBuilder = form(context, findViewById(R.id.recyclerView)) {
            email(Tag.Email.ordinal) {
                title = getString(R.string.email)
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
            password(Tag.Password.ordinal) {
                title = getString(R.string.password)
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
        }
    }
}