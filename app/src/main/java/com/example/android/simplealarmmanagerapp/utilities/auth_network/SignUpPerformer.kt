package com.example.android.simplealarmmanagerapp.utilities.auth_network

import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.utilities.constants.JWT_HEADER_NAME
import com.example.android.simplealarmmanagerapp.utilities.constants.SIGN_UP_RESULT_HEADER_NAME
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import khttp.post
import khttp.responses.Response

class SignUpPerformer(private val authSubscriber: AuthSubscriber, private val url: String):
        AsyncTask<Account, String, Response>() {
    val TAG = "SignUpPerformer"
    var account: Account? = null

    fun sendRequest(account: Account?): Response {
        val data = account?.getJSON()
        return post(url, data=data)
    }

    override fun doInBackground(vararg accounts: Account?): Response {
        account = accounts[0]
        val response = sendRequest(account)
        // Logging.
        Log.i(TAG, "Sign-up account: $account")
        Log.i(TAG, "Student sign-up. Response: $response")
        return response
    }

    override fun onPostExecute(response: Response) {
        val signUpStatusResult: Resource<Account>
        var jwt: String? = null
        if (validSignUpResponse(response)) {
            // Logging.
            Log.i(TAG, "Successful account sign up.")

            jwt = response.headers[JWT_HEADER_NAME].toString()

            // Extracting id and type of account.
            val json: JsonObject = Parser().parse(StringBuilder(response.text)) as JsonObject
            account!!.id = json["id"].toString().toInt()
            account!!.type = json["type"].toString()
            signUpStatusResult = Resource.success(account!!)
        } else {
            val json: JsonObject = Parser().parse(StringBuilder(response.text)) as JsonObject
            val errorMessage = json[SIGN_UP_RESULT_HEADER_NAME].toString()
            // Logging.
            Log.i(TAG, "Failed account sign up: $errorMessage")
            signUpStatusResult = Resource.error(account!!, errorMessage)
        }
        authSubscriber.handleAuthUpdate(Pair(signUpStatusResult, jwt))
    }

    fun validSignUpResponse(response: Response) : Boolean {
        return response.statusCode == 201
    }
}