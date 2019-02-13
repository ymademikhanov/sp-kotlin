package com.example.android.simplealarmmanagerapp.models.repositories

import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDao
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

class AttCheckReportRepositoryImpl(private val remoteDao: AttCheckReportDao?,
                               private val localDao: AttCheckReportDao?)
    : AttCheckReportRepository {
    override fun report(report: AttendanceCheckReport) {
        localDao?.report(report)
    }
}