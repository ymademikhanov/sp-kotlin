package com.example.android.simplealarmmanagerapp.models.daos

import androidx.room.*
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

@Dao
abstract class AttendanceCheckReportDao: BaseDao<AttendanceCheckReport> {
    @Query("SELECT * FROM attendance_check_report")
    abstract fun getAll(): List<AttendanceCheckReport>

    @Query("SELECT * FROM attendance_check_report WHERE reported == 0")
    abstract fun getUnreported(): List<AttendanceCheckReport>

    @Transaction
    open fun updateAll(reports: List<AttendanceCheckReport>) {
        deleteAll()
        insertAll(reports)
    }

    @Insert
    abstract fun insertAll(reports: List<AttendanceCheckReport>)

    @Query("DELETE FROM attendance_check_report")
    abstract fun deleteAll()
}