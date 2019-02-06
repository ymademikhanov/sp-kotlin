package com.example.android.simplealarmmanagerapp.schedulers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.receivers.NetworkDisabler
import com.example.android.simplealarmmanagerapp.utilities.constants.WIFI_DISABLING_SERVICE_PREFIX
import com.example.android.simplealarmmanagerapp.utilities.getDateTime

class WifiDisableScheduler {
    companion object {
        val TAG = "WifiDisableScheduler"
        fun schedule(mContext: Context,
                     alarmManager: AlarmManager,
                     classes: ArrayList<Class>) {
            for (c in classes) {
                val intent = Intent(mContext, NetworkDisabler::class.java)
                val alarmId = "$WIFI_DISABLING_SERVICE_PREFIX:${c.sectionId}:${c.id}"
                val alarmIdHashcode = alarmId.hashCode()
                val pendingIntent = PendingIntent.getBroadcast(
                        mContext,
                        alarmIdHashcode,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                )

                val endTime = c.end
                alarmManager.setExact(AlarmManager.RTC, endTime, pendingIntent)
                Log.i(TAG, "Scheduled Starting Class Alarm at ${getDateTime(endTime)}")
            }
        }
    }
}