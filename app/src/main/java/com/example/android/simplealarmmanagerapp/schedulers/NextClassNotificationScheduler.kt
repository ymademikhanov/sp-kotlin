package com.example.android.simplealarmmanagerapp.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.receivers.NextClassNotifier
import com.example.android.simplealarmmanagerapp.utilities.constants.CLASS_NOTIFICATION_PREFIX
import com.example.android.simplealarmmanagerapp.utilities.constants.SECTION_COURSE_TITLE
import com.example.android.simplealarmmanagerapp.utilities.getDateTime

class NextClassNotificationScheduler {
    companion object {
        val TAG = "NextClassNotify"
        fun schedule(mContext: Context,
                     alarmManager: AlarmManager,
                     sectionCourseTitle: String,
                     classes: ArrayList<Class>) {

            Log.i(TAG, "Section course title $sectionCourseTitle")

            // CREATING ALARM MANAGER SCHEDULES FOR STARTING CLASS NOTIFICATION AND TURNING ON WIFI.
            for (c in classes) {
                if (c.start < System.currentTimeMillis())
                    continue
                val intent = Intent(mContext, NextClassNotifier::class.java)
                intent.putExtra("startingClassTitle", sectionCourseTitle)
                val alarmId = "$CLASS_NOTIFICATION_PREFIX:${c.sectionId}:${c.id}"
                val hash = alarmId.hashCode()
                val pendingIntent = PendingIntent
                        .getBroadcast(mContext, hash, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                val fiveMinBeforeStart = c.start - 2000 * 60
                alarmManager.setExact(AlarmManager.RTC, fiveMinBeforeStart, pendingIntent)
                Log.i(TAG, "Scheduled Next Class Alarm at ${getDateTime(fiveMinBeforeStart)}")
            }
        }
    }
}