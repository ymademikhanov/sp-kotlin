package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

class AttendanceCheck (val id: Int? = null,
                       @SerializedName("attendance_id") val attendanceId: Int? = null,
                       val timestamp: Long = 0,
                       val checked: Boolean? = null)

{
    override fun toString(): String {
        return "Attendance Check with id: $id, time: $timestamp"
    }
}
