package com.example.android.simplealarmmanagerapp.models.daos

import android.util.Log
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class AttCheckReportDaoRemote(private val jwt: Map<String, String>,
                              private val retrofitClient: Retrofit) : AttCheckReportDao {

    lateinit var mSubscriber: AttCheckDaoSubscriber

    override fun setSubscriber(subscriber: AttCheckDaoSubscriber) {
        mSubscriber = subscriber
    }

    override fun report(report: AttendanceCheck) {
        val client = retrofitClient.create<StudentAPI>(StudentAPI::class.java)
        client.reportAttendanceCheck(jwt, report.id!!, report).enqueue(
            object : Callback<AttendanceCheck> {
                override fun onResponse(call: Call<AttendanceCheck>, response: Response<AttendanceCheck>) {
                    if (response.isSuccessful) {
                        val attendanceCheck = response.body()
                        mSubscriber.updated(Resource.success(attendanceCheck!!))
                    } else {
                        mSubscriber.updated(Resource.error(null, response.errorBody().toString()))
                    }
                }

                override fun onFailure(call: Call<AttendanceCheck>, t: Throwable) {
                    // something went completely south (like no internet connection)
                    mSubscriber.updated(Resource.error(null, t.message.toString()))
                    Log.d("Error", t.message)
                }
            }
        )
    }
}