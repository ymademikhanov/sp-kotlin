package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

class Attendance(val id: Int? = null,
                 @SerializedName("student_id") val studentId: Int? = null,
                 @SerializedName("class_id") val classId: Int? = null,
                 val attended: Boolean? = null,
                 val updatedAt: Timestamp? = null,
                 val createdAt: Timestamp? = null) {

    private val checks: List<AttendanceCheck>? = null
}