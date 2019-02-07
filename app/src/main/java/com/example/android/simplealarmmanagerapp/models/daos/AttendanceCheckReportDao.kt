package com.example.android.simplealarmmanagerapp.models.daos

import androidx.room.*
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

@Dao
interface AttendanceCheckReportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attendanceCheckReport: AttendanceCheckReport)

    @Update
    fun update(attendanceCheckReport: AttendanceCheckReport)

    @Delete
    fun delete(attendanceCheckReport: AttendanceCheckReport)

    @Query("SELECT * FROM attendance_check_report")
    fun getAll(): List<AttendanceCheckReport>

    @Query("SELECT * FROM attendance_check_report WHERE reported == 0")
    fun getUnreported(): List<AttendanceCheckReport>
}