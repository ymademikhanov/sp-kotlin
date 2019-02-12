package com.example.android.simplealarmmanagerapp.models.daos

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import java.util.concurrent.Executors

@Database(entities = [AttendanceCheckReport::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun attendanceCheckReportDao(): AttendanceCheckReportDao

    companion object {
        var instance: AppDatabase? = null
        fun getAppDatabase(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "attendanceCheckDatabase")
                            .addCallback(object : Callback() {
                                override fun onCreate(db: SupportSQLiteDatabase) {
                                    super.onCreate(db)

                                    // moving to a new thread
                                    Executors.newSingleThreadExecutor().execute {
                                        val timestamp = System.currentTimeMillis()
                                        val dummyReport = AttendanceCheckReport(
                                                attendanceCheckID = 1,
                                                timestamp = timestamp,
                                                reported = false,
                                                foundDevice = "android"
                                        )

                                        getAppDatabase(context)!!
                                                .attendanceCheckReportDao()
                                                .insert(dummyReport)

                                        Log.i("ROOM", "inserting")
                                    }
                                }
                            })
                            .build()
                }
            }
            return instance
        }

        fun destroyDataBase(){
            instance = null
        }
    }
}