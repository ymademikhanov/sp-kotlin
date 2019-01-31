package com.example.android.simplealarmmanagerapp.utilities

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser

class JwtToJson {
    companion object {
        fun convert(jwt: String) : JsonObject {
            val splitString = jwt.split(".")
            val base64EncodedBody = splitString[1]
            val body = android.util.Base64.decode(base64EncodedBody, android.util.Base64.DEFAULT);
            val bodyStr = String(body)
            val parser = Parser()
            val stringBuilder = StringBuilder(bodyStr)
            val json: JsonObject = parser.parse(stringBuilder) as JsonObject
            return json
        }
    }
}