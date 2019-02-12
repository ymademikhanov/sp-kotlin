package com.example.android.simplealarmmanagerapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPIClient
import com.example.android.simplealarmmanagerapp.models.Section
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment() {

    val TAG = "HomeFragment"
    lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = context!!.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        loadSectionsWithAttendances()
    }

    private fun loadSectionsWithAttendances() {
        val jwt = preferences.getString("jwt", "")
        val jwtMap = mapOf("x-auth" to jwt)

        val client = StudentAPIClient.client.create<StudentAPI>(StudentAPI::class.java)
        val sections = client.listSectionsWithAttendance(jwtMap).enqueue(object : Callback<List<Section>> {
            override fun onResponse(call: Call<List<Section>>, response: Response<List<Section>>) {
                if (response.isSuccessful()) {
                    // tasks available
                    val sections = response.body()
                    Log.i(TAG, "Sections with attendances $sections")

                    for (section in sections!!) {
                        Log.i(TAG, "passed ${section.passedClasses}, attended ${section.attendedClasses}")
                    }
                } else {
                    // error response, no access to resource?
                }
            }

            override fun onFailure(call: Call<List<Section>>, t: Throwable) {
                // something went completely south (like no internet connection)
                Log.d("Error", t.message)
            }
        })
    }
}
