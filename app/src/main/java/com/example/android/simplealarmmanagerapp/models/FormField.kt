package com.example.android.simplealarmmanagerapp.models

import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout

enum class FormFieldType {
    EMAIL, PASSWORD, NAME, TEXT
}

class FormField {
    var editText: EditText
    lateinit var validator: (String) -> Boolean

    constructor(context: Context, type: FormFieldType, hint: String) {
        editText = EditText(context)
        editText.setHint(hint)
        when (type) {
            FormFieldType.PASSWORD -> {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD)
            }
            FormFieldType.EMAIL -> {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
            }
            FormFieldType.NAME -> {
                editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
            }
            FormFieldType.TEXT -> {
                editText.setInputType(InputType.TYPE_CLASS_TEXT)
            }
        }

        var editTextParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        editText.layoutParams = editTextParams
    }

    fun getText() : String {
        return editText.text.toString()
    }
}