package com.example.android.simplealarmmanagerapp.models.repositories

import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

interface AttCheckReportRepository {
    fun report(report: AttendanceCheckReport)
}