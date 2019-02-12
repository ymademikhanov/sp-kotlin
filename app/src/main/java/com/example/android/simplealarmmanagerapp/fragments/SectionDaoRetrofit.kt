package com.example.android.simplealarmmanagerapp.fragments

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPIClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SectionDaoRetrofit: SectionDao {

    val TAG = "SectionDaoRetrofit"

    private val client = StudentAPIClient.client.create<StudentAPI>(StudentAPI::class.java)
    private val sectionsResource = MutableLiveData<Resource<List<Section>>>()

    override fun refresh(jwt: Map<String, String>) {
        loadInBackground(jwt)
    }

    override fun getCourses(jwt: Map<String, String>): LiveData<Resource<List<Section>>> {
        loadInBackground(jwt)
        return sectionsResource
    }

    fun loadInBackground(jwt: Map<String, String>) {
        client.listSectionsWithAttendance(jwt).enqueue(object : Callback<List<Section>> {
            override fun onResponse(call: Call<List<Section>>, response: Response<List<Section>>) {
                if (response.isSuccessful) {
                    val sectionList = response.body()
                    sectionsResource.value = Resource.success(sectionList!!)

                    // Logging.
                    Log.i(TAG, "Sections $sectionList")
                } else {
                    sectionsResource.value =
                            Resource.error(null, response.errorBody().toString())

                    // Logging.
                    Log.i(TAG, "Unsuccessful response ${response.errorBody().toString()}")
                }
            }

            override fun onFailure(call: Call<List<Section>>, t: Throwable) {
                // something went completely south (like no internet connection)
                sectionsResource.value = Resource.error(null, "Failure")
                Log.d("Request error", t.message)
            }
        })
    }
}