package com.example.android.simplealarmmanagerapp.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.receivers.BeaconScanner
import com.example.android.simplealarmmanagerapp.utilities.constants.ATTENDANCE_CHECK_ALARM_PREFIX
import com.example.android.simplealarmmanagerapp.utilities.getDateTime

class AttendanceCheckScheduler {
    companion object {
        val TAG = "AttendCheckScheduler"
        fun schedule(mContext: Context,
                     alarmManager: AlarmManager,
                     checks: ArrayList<AttendanceCheck>) {
            // CREATING ALARM MANAGER SCHEDULES FOR ATTENDANCE CHECKS.
            for (check in checks) {
                val intent = Intent(mContext, BeaconScanner::class.java)
                intent.putExtra("attendanceId", check.attendanceId)
                intent.putExtra("attendanceCheckId", check.id)

                val alarmId = "$ATTENDANCE_CHECK_ALARM_PREFIX:${check.attendanceId}:${check.id}"
                val hashcode = alarmId.hashCode()
                val pendingIntent = PendingIntent.getBroadcast(
                        mContext,
                        hashcode,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

                alarmManager.setExact(AlarmManager.RTC, check.timestamp, pendingIntent)
                Log.i(TAG, "Scheduled Alarm at ${getDateTime(check.timestamp)}")
            }
        }
    }
}