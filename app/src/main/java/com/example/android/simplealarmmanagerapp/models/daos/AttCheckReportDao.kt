package com.example.android.simplealarmmanagerapp.models.daos

import com.example.android.simplealarmmanagerapp.models.AttendanceCheck

interface AttCheckReportDao {
    fun setSubscriber(subscriber: AttCheckDaoSubscriber)
    fun report(report: AttendanceCheck)
}