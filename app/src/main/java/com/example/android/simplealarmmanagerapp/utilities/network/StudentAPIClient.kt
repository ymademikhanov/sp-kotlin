package com.example.android.simplealarmmanagerapp.utilities.network

import com.google.gson.ExclusionStrategy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudentAPIClient {
    companion object {

        val baseURL: String = "https://attendance-app-dev.herokuapp.com/api/v1/"
        var retrofit: Retrofit? = null

        val client: Retrofit
            get() {
                if (retrofit == null) {
                    val gson = GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()
                    retrofit = Retrofit.Builder()
                            .baseUrl(baseURL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build()
                }
                return retrofit!!
            }
    }
}