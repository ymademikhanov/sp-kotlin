package com.example.android.simplealarmmanagerapp.models.daos

import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

interface AttCheckReportDao {
    fun report(report: AttendanceCheckReport)
}