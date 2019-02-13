package com.example.android.simplealarmmanagerapp.models.repositories

import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckDaoSubscriber
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDao
import com.example.android.simplealarmmanagerapp.utilities.network.Resource

class AttCheckReportRepositoryImpl(private val remoteDao: AttCheckReportDao?,
                               private val localDao: AttCheckReportDao?)
    : AttCheckReportRepository, AttCheckDaoSubscriber {

    lateinit var pendingCheck: AttendanceCheck

    override fun report(check: AttendanceCheck) {
        pendingCheck = check
        remoteDao?.setSubscriber(this)
        remoteDao?.report(check)
    }

    override fun updated(report: Resource<AttendanceCheck>) {
        when(report.status) {
            Resource.Status.SUCCESS -> {
                // On successful remote report, we do not write anything to local database.
            }
            Resource.Status.ERROR -> {
                localDao?.report(pendingCheck)
            }
        }
    }
}