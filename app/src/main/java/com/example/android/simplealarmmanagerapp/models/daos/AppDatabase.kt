package com.example.android.simplealarmmanagerapp.models.daos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport

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