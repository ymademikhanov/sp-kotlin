package com.example.android.simplealarmmanagerapp.receivers

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.util.Log
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.*
import com.example.android.simplealarmmanagerapp.utilities.network.NetworkManager
import java.util.*
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPIClient
import com.example.android.simplealarmmanagerapp.models.*
import com.example.android.simplealarmmanagerapp.schedulers.AttendanceCheckScheduler
import com.example.android.simplealarmmanagerapp.schedulers.NextClassNotificationScheduler
import com.example.android.simplealarmmanagerapp.schedulers.WifiDisableScheduler


class WeeklyBTCheckScheduler : BroadcastReceiver() {
    val TAG = "AttWeeklyBTCheckSched"

    private var classes: ArrayList<Class> = ArrayList()

    lateinit var preferences: SharedPreferences
    lateinit var alarmManager : AlarmManager
    lateinit var mContext: Context
    lateinit var notificationManager: NotificationManager
    lateinit var notificationBuilder: Notification.Builder

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        mContext = context!!

        // Enabling WiFi.
        NetworkManager.enableNetwork(context)

        // Starting notification.
        notificationBuilder = Notification.Builder(context)
                .setContentTitle(APPLICATION_NAME)
                .setContentText(ATTENDANCE_CHECK_SETUP_START_MESSAGE)
                .setSmallIcon(R.drawable.ic_action_attendance_info)

        notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())

        preferences = context
                .applicationContext
                .getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        val jwt = preferences.getString("jwt", "")

        loadAndScheduleAttendanceChecks(jwt)
    }

    fun loadAndScheduleAttendanceChecks(jwt: String) {
        LoadAndScheduleAttendanceInBackground().execute(jwt)
    }

    inner class LoadAndScheduleAttendanceInBackground:
            AsyncTask<String, String, ArrayList<AttendanceCheck>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            classes.clear()
        }

        override fun doInBackground(vararg jwts: String): ArrayList<AttendanceCheck> {
            // LOADING COURSE SECTIONS FOR STUDENT.
            val jwtMap = mapOf("x-auth" to jwts[0])

            val client = StudentAPIClient.client.create<StudentAPI>(StudentAPI::class.java)
            val sections = client.listSections(jwtMap).execute().body()

            for (section in sections!!) {
                val classService = client.listClasses(jwtMap, section.id!!)
                val temp = classService.execute().body()
                classes.addAll(temp!!)
            }

            val attendances: ArrayList<Attendance> = ArrayList()
            for (c in classes) {
                val temp = client.listAttendances(jwtMap, c.id!!).execute().body()
                attendances.addAll(temp!!)
            }

            val attendanceChecks: ArrayList<AttendanceCheck> = ArrayList()
            for (attendance in attendances) {
                val temp = client.listChecks(jwtMap, attendance.id!!).execute().body()
                attendanceChecks.addAll(temp!!)
            }
            return attendanceChecks
        }

        override fun onPostExecute(attendanceChecks: ArrayList<AttendanceCheck>) {
            // ALARM MANAGER.
            alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Scheduling services.
            val courseTitle = preferences.getString(SECTION_COURSE_TITLE, "")

            // Scheduling next class notifier.
            NextClassNotificationScheduler.schedule(mContext, alarmManager, courseTitle, classes)

            // Scheduling wifi disabling after a class.
//            WifiDisableScheduler.schedule(mContext, alarmManager, classes)

            // Scheduling attendance checks for the next week.
            AttendanceCheckScheduler.schedule(mContext, alarmManager, attendanceChecks)

            // Disabling Wifi.
//            NetworkManager.disableNetwork(mContext)

            notificationBuilder.setContentText(ATTENDANCE_CHECK_SETUP_END_MESSAGE)
            notificationManager.notify(0, notificationBuilder.build())
        }
    }
}