package com.example.android.simplealarmmanagerapp.models.entities

import androidx.room.*

@Entity(tableName = "attendance_check_report")
data class AttendanceCheckReport(
        @PrimaryKey(autoGenerate = true)
        val id: Int,

        @ColumnInfo(name = "attendance_check_id")
        val attendanceCheckID: Int,

        val timestamp: Long,

        val reported: Boolean,

        @ColumnInfo(name = "found_device")
        val foundDevice: String
)