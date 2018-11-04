package com.example.android.simplealarmmanagerapp.models

import android.content.Context
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import android.widget.LinearLayout

enum class FormFieldType {
    EMAIL, PASSWORD, NAME, TEXT, NUMBER
}

class FormField {
    var editText: EditText
    lateinit var validator: (String) -> Boolean

    constructor(context: Context, type: FormFieldType, hint: String) {
        editText = EditText(context)
        editText.hint = hint
        when (type) {
            FormFieldType.PASSWORD -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                editText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            FormFieldType.EMAIL -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            }
            FormFieldType.NAME -> {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
            }
            FormFieldType.TEXT -> {
                editText.inputType = InputType.TYPE_CLASS_TEXT
            }
            FormFieldType.NUMBER -> {
                editText.inputType = InputType.TYPE_NUMBER_VARIATION_NORMAL
            }
        }

        var editTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        editText.layoutParams = editTextParams
    }

    fun getText() : String {
        return editText.text.toString()
    }
}