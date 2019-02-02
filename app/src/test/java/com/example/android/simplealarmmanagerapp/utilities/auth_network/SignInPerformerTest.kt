package com.example.android.simplealarmmanagerapp.utilities.auth_network

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.utilities.constants.SIGN_IN_URL
import org.junit.Assert
import org.junit.Test


class SignInPerformerTest {
    @Test
    fun successfulSignIn() {
        val validAccount = Account("yer@nu.edu.kz", "qwerty", null, null)
        val signInPerformer = SignInPerformer(null, SIGN_IN_URL)

        // Running.
        val response = signInPerformer.sendRequest(validAccount)

        Assert.assertEquals(response.statusCode, 200)
        Assert.assertEquals(response.text, "\"\"")
    }

    @Test
    fun failedSignInWrongEmail() {
        val validAccount = Account("random@random.kz", "qwerty", null, null)
        val signInPerformer = SignInPerformer(null, SIGN_IN_URL)

        // Running.
        val response = signInPerformer.sendRequest(validAccount)
        val json: JsonObject = Parser().parse(StringBuilder(response.text)) as JsonObject
        val actualErrorMessage = json["message"].toString()
        val expectedErrorMessage = "Account with email ${validAccount.email} not found"

        Assert.assertEquals(response.statusCode, 400)
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage)
    }

    @Test
    fun failedSignInWrongPassword() {
        val validAccount = Account("yer@nu.edu.kz", "wront_password", null, null)
        val signInPerformer = SignInPerformer(null, SIGN_IN_URL)

        // Running.
        val response = signInPerformer.sendRequest(validAccount)
        val json: JsonObject = Parser().parse(StringBuilder(response.text)) as JsonObject
        val actualErrorMessage = json["message"].toString()
        val expectedErrorMessage = "Password is incorrect"

        Assert.assertEquals(response.statusCode, 400)
        Assert.assertEquals(actualErrorMessage, expectedErrorMessage)
    }
}