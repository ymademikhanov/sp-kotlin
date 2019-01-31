package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.R.id.recyclerView
import com.thejuki.kformmaster.helper.FormBuildHelper
import com.thejuki.kformmaster.model.BaseFormElement
import com.thejuki.kformmaster.model.FormEmailEditTextElement

import com.thejuki.kformmaster.model.FormPasswordEditTextElement

class YetAnotherAuthActivity : AppCompatActivity() {
    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_yet_another_auth)
        context = this
        // Initializing UI.
        initUI()
    }

    private enum class Tag {
        Email,
        Password
    }

    private fun initUI() {

        // Initialize variables
        val formBuilder = FormBuildHelper(context)
        formBuilder.attachRecyclerView(this, findViewById(recyclerView))

        val elements: MutableList<BaseFormElement<*>> = mutableListOf()

        // Declare form elements
        val emailElement = FormEmailEditTextElement(Tag.Email.ordinal).apply {
            title = getString(R.string.email)
        }

        elements.add(emailElement)

        val passwordElement = FormPasswordEditTextElement(Tag.Password.ordinal).apply {
            title = getString(R.string.password)
        }

        elements.add(passwordElement)

        // Add form elements (Form is refreshed for you)
        formBuilder.addFormElements(elements)

    }
}