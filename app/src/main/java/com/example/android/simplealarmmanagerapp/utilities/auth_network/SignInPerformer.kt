package com.example.android.simplealarmmanagerapp.utilities.auth_network

import android.os.AsyncTask
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.utilities.JwtToJson
import com.example.android.simplealarmmanagerapp.utilities.constants.JWT_HEADER_NAME
import com.example.android.simplealarmmanagerapp.utilities.network.APIClient
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import khttp.post
import khttp.responses.Response

class SignInPerformer(private val authSubscriber: AuthSubscriber?, private val url: String):
        AsyncTask<Account, String, Response>() {
    val TAG = "SignInPerformer"
    var account: Account? = null

    fun sendRequest(account: Account?): Response {
        val data = account?.getJSON()
        return post(url, data= data)
    }

    override fun doInBackground(vararg accounts: Account?): Response {
        account = accounts[0]
        val response = sendRequest(account)

        // Logging.
        Log.i(TAG, "Sign-in account: $account")
        Log.i(TAG, "Response: $response")

        return response
    }

    override fun onPostExecute(response: Response) {
        val signInStatusResult: Resource<Account>
        var jwt: String? = null
        if (validSignInResponse(response)) {
            jwt = response.headers[JWT_HEADER_NAME].toString()
            val json = JwtToJson.convert(jwt)
            account?.id = json.int("account_id")!!
            account?.type = json.string("account_type")
            signInStatusResult = Resource.success(account!!)
        } else {
            val json: JsonObject = Parser().parse(StringBuilder(response.text)) as JsonObject
            val errorMessage = json["message"].toString()
            signInStatusResult = Resource.error(null, errorMessage)
        }
        authSubscriber!!.handleAuthUpdate(Pair(signInStatusResult, jwt))
    }

    // Helper methods.
    fun validSignInResponse(response: Response) : Boolean {
        return response.statusCode == 200 && response.text == "\"\""
    }
}