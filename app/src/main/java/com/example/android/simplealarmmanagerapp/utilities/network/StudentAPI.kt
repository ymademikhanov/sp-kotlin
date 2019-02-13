package com.example.android.simplealarmmanagerapp.utilities.network

import com.example.android.simplealarmmanagerapp.models.Attendance
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.models.Section
import retrofit2.Call
import retrofit2.http.*

interface StudentAPI {
    @GET("me/sections")
    fun listSections(@HeaderMap headers: Map<String, String>): Call<List<Section>>

    @GET("me/attendances")
    fun listSectionsWithAttendance(@HeaderMap headers: Map<String, String>): Call<List<Section>>

    @GET("sections/{sectionID}/classes")
    fun listClasses(@HeaderMap headers: Map<String, String>,
                           @Path("sectionID") sectionID: Int): Call<List<Class>>

    @GET("classes/{classID}/attendances")
    fun listAttendances(@HeaderMap headers: Map<String, String>,
                            @Path("classID") classID: Int): Call<List<Attendance>>

    @GET("attendances/{attendanceID}/checks")
    fun listChecks(@HeaderMap headers: Map<String, String>,
                            @Path("attendanceID") sectionID: Int): Call<List<AttendanceCheck>>

    @PATCH("attendances/checks/{id}")
    fun reportAttendanceCheck(@HeaderMap headers: Map<String, String>, @Path("id") id: Int)
}