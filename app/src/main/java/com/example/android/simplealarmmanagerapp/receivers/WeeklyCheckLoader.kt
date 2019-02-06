package com.example.android.simplealarmmanagerapp.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.utilities.constants.WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX
import khronos.*

class WeeklyCheckLoader {
    companion object {
        val TAG = "WeeklyCheckLoader"
        fun schedule(context: Context, account: Account) {
            val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            Log.i(TAG, "Setting weekly attendance checks scheduling...")

            if (account.type == "student") {
                // SETTING REPEATED ATTENDANCE CHECK LOADING ON EVERY SUNDAY.
                val today = Dates.today
                var thisSunday = today.with(weekday = 1)
                thisSunday = thisSunday.beginningOfHour

                thisSunday = today
                thisSunday += 1.minute

                // Starting from past week which means that alarm manager will immediately fire.
//                thisSunday -= 1.week

                for (i in 0..15) {
                    if (thisSunday.time > System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                        val intent = Intent(context, WeeklyBTCheckScheduler::class.java)
                        val alarmId = "$WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX:$account.id:$i"
                        val hashcode = alarmId.hashCode()
                        val pendingIntent = PendingIntent.getBroadcast(
                                context,
                                hashcode,
                                intent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        )

                        alarmManager.setExact(AlarmManager.RTC, thisSunday.time, pendingIntent)
                        Log.i(TAG, "Scheduled an attendance checks loading on $thisSunday")
                    }
                    thisSunday += 1.week
                }
            }
        }
    }
}