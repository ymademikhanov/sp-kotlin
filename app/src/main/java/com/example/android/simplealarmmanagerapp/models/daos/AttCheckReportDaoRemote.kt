package com.example.android.simplealarmmanagerapp.models.daos

import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import retrofit2.Retrofit

class AttCheckReportDaoRemote(private val client: Retrofit) : AttCheckReportDao {

    override fun report(report: AttendanceCheckReport) {

    }
}