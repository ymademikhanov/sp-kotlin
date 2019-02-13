package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

class Attendance(
        @Expose
        val id: Int? = null,

        @Expose
        @SerializedName("student_id")
        val studentId: Int? = null,

        @Expose
        @SerializedName("class_id")
        val classId: Int? = null,

        @Expose
        val attended: Boolean? = null,

        @Expose
        @SerializedName("updated_at")
        val updatedAt: Long? = null,

        @Expose
        @SerializedName("created_at")
        val createdAt: Long? = null
) {
    private val checks: List<AttendanceCheck>? = null
}