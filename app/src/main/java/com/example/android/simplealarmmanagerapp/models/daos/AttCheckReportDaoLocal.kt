package com.example.android.simplealarmmanagerapp.models.daos

import android.content.Context
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AttCheckReportDaoLocal(val context: Context): AttCheckReportDao {
    val TAG = "AttendanceCheckReporter"
    var db: AppDatabase? = null
    var reportDao: AttendanceCheckReportDao? = null

    override fun report(report: AttendanceCheckReport) {
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