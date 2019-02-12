package com.example.android.simplealarmmanagerapp.utilities.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StudentAPIClient {
    companion object {

        val baseURL: String = "https://attendance-app-dev.herokuapp.com/api/v1/"
        var retrofit: Retrofit? = null

        val client: Retrofit
            get() {

                if (retrofit == null) {
                    retrofit = Retrofit.Builder()
                            .baseUrl(baseURL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()
                }
                return retrofit!!
            }
    }
}