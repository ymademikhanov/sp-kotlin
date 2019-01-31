package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.R.id.recyclerView
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_COLOR
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_DARK_COLOR
import com.thejuki.kformmaster.helper.*
import java.text.SimpleDateFormat
import java.util.*

class YetAnotherAuthActivity : AppCompatActivity() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        context = this
        // Initializing UI.
        initUI()
    }

    private enum class Tag {
        Email,
        Password,
        Text,
        Number,
    }

    private fun initUI() {

        // Initialize variables
        val formBuilder = form(context, findViewById(recyclerView)) {
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
            text(Tag.Text.ordinal) {
                title = getString(R.string.firstname)
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
            text(Tag.Text.ordinal) {
                title = getString(R.string.lastname)
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
            text(Tag.Text.ordinal) {
                title = "Office"
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
            phone(Tag.Number.ordinal) {
                title = "Phone"
                titleTextColor = PRIMARY_COLOR
                titleFocusedTextColor = PRIMARY_DARK_COLOR
            }
            button(1) {
                value = "Sign up"
                backgroundColor = PRIMARY_COLOR
                valueTextColor = Color.WHITE

                valueObservers.add({ _, _ ->
                    // Nothing was changed but the button was tapped!
                })
            }
        }
    }
}