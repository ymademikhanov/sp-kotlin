package com.example.android.simplealarmmanagerapp.auth_network

import android.os.AsyncTask
import android.util.Log
import khttp.post
import org.json.JSONObject

class LoginPerformer : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg urls: String?): String {
        val email = urls[0]
        val password = urls[1]

        val payload = mapOf("email" to email, "password" to password)
        var r = post("https://attendance-app-dev.herokuapp.com/api/v1/auth/signin", data= JSONObject(payload))

        return r.headers.get("X-Auth").toString()
    }

    override fun onPostExecute(result: String?) {
        Log.i("DoInBackground", result)
    }
}