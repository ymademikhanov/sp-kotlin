package com.example.android.simplealarmmanagerapp.models.repositories

import com.example.android.simplealarmmanagerapp.models.AttendanceCheck

interface AttCheckReportRepository {
    fun report(report: AttendanceCheck)
}