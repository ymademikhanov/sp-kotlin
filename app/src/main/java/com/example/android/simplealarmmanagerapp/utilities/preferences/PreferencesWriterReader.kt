package com.example.android.simplealarmmanagerapp.utilities.preferences

import android.content.SharedPreferences

fun removeUser(preferences: SharedPreferences) {
    val editor = preferences.edit()
    editor.remove("email")
    editor.remove("password")
    editor.remove("signedIn")
    editor.remove("jwt")
    editor.apply()
}