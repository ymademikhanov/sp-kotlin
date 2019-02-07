package com.example.android.simplealarmmanagerapp.database.attendance_check_report

import android.content.Context
import androidx.room.Room
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.example.android.simplealarmmanagerapp.models.daos.AppDatabase
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import org.junit.Assert
import org.junit.Before
import androidx.test
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AttendanceCheckReportDatabaseTests {
    lateinit var context: Context
    var database: AppDatabase? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @Test
    fun successfulSignIn() {
        Assert.assertNotNull(database)

        val report = AttendanceCheckReport(
                1,
                2,
                100000,
                false,
                "34:12")

        // Performing insertion.
        database?.attendanceCheckReportDao()?.insert(report)

        val reports = database?.attendanceCheckReportDao()?.getAll()

        Assert.assertEquals(report, reports?.first())
    }
}