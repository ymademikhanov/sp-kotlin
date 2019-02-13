package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

class AttendanceCheck (
        @Expose(serialize = false)
        val id: Int? = null,

        @SerializedName("attendance_id")
        @Expose
        val attendanceId: Int? = null,

        @Expose
        val timestamp: Long = 0,

        @Expose
        val checked: Boolean? = null)
{
    override fun toString(): String {
        return "Attendance Check with id: $id, attendanceId: ${attendanceId},  time: $timestamp"
    }
}
