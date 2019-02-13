package com.example.android.simplealarmmanagerapp.models.daos

import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.utilities.network.Resource

interface AttCheckDaoSubscriber {
    fun updated(report: Resource<AttendanceCheck>)
}