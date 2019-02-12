package com.example.android.simplealarmmanagerapp.services

import android.content.Context
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.daos.AppDatabase
import com.example.android.simplealarmmanagerapp.models.daos.AttendanceCheckReportDao
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AttendanceCheckReporter {
    companion object {
        val TAG = "AttendanceCheckReporter"
        lateinit var database: AppDatabase

        var db: AppDatabase? = null
        var reportDao: AttendanceCheckReportDao? = null

        fun report(context: Context, report: AttendanceCheckReport) {
            Observable.fromCallable {
                db = AppDatabase.getAppDatabase(context = context)
                reportDao = db?.attendanceCheckReportDao()
                with(reportDao){
                    this?.insert(report)
                }
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

            Observable.fromCallable {
                db = AppDatabase.getAppDatabase(context = context)
                reportDao = db?.attendanceCheckReportDao()
                this.db?.attendanceCheckReportDao()?.getUnreported()
            }.doOnNext { list ->
                var finalString = ""
                list?.map { finalString+= "Attendance check report: " + it.toString() + "\n"}
                Log.i(TAG, "All attendance check reports: \n $finalString")
            }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
        }
    }
}