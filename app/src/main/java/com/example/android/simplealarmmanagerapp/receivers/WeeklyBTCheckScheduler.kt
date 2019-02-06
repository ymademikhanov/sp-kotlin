package com.example.android.simplealarmmanagerapp.receivers

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.util.Log
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.models.Attendance
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.utilities.constants.*
import com.example.android.simplealarmmanagerapp.utilities.getDateTime
import com.example.android.simplealarmmanagerapp.utilities.network.NetworkManager
import com.google.gson.Gson
import java.util.*

class WeeklyBTCheckScheduler : BroadcastReceiver() {
    val TAG = "WeeklyBTCheckScheduler"

    private var classList: ArrayList<Class> = ArrayList()
    private var sectionList: ArrayList<Section> = ArrayList()
    private var attendanceList: ArrayList<Attendance> = ArrayList()
    private var attendanceCheckList: ArrayList<AttendanceCheck> = ArrayList()

    lateinit var preferences: SharedPreferences
    lateinit var alarmManager : AlarmManager
    lateinit var mContext: Context

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        mContext = context!!

        // Enabling WiFi.
        NetworkManager.enableNetwork(context)

        // Starting notification.
        val builder = Notification.Builder(context)
                .setContentTitle("Attendance checks")
                .setContentText("Loading attendance checks for the next week")
                .setSmallIcon(R.drawable.ic_action_attendance_info)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())

        preferences = context.applicationContext.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val jwt = preferences.getString("jwt", "")
        LoadAndScheduleAttendanceInBackground().execute(jwt)
    }

    inner class LoadAndScheduleAttendanceInBackground: AsyncTask<String, String, ArrayList<AttendanceCheck>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            classList.clear()
        }

        override fun doInBackground(vararg jwts: String): ArrayList<AttendanceCheck> {
            // LOADING COURSE SECTIONS FOR STUDENT.
            val jwt = jwts[0]
            val response = khttp.get(MY_SECTION_URL, headers=mapOf("x-auth" to jwt))
            Log.i(TAG, "Response for loading sections: ${response.jsonArray}")

            val sections = response.jsonArray
            for (i in 0..(sections.length() - 1)) {
                val obj = sections.getJSONObject(i)
                val objectJSONString = obj.toString()
                val section = Gson().fromJson(objectJSONString, Section::class.java)
                Log.i(TAG, "Object JSON of section: $objectJSONString")
                Log.i(TAG, "Section: $section")
                sectionList.add(section)
            }

            // LOADING COURSE SECTION CLASSES FOR STUDENT.
            for (section in sectionList) {
                val sectionId = section.id
                val url = "$SECTIONS_URL/$sectionId/classes"
                Log.i(TAG, "Url: $url")
                val response = khttp.get(url, headers=mapOf("x-auth" to jwt))
                Log.i(TAG, "Response: $response")
                Log.i(TAG, "Response: ${response.jsonArray}")

                val classesJSONArray = response.jsonArray
                for (i in 0..(classesJSONArray.length() - 1)) {
                    val objStr = classesJSONArray.getJSONObject(i).toString()
                    val universityClass = Gson().fromJson(objStr, Class::class.java)
                    Log.i(TAG, "Class: $universityClass")
                    if (universityClass.end > System.currentTimeMillis()) {
                        classList.add(universityClass)
                    }
                }
            }

            for (c in classList) {
                // LOADING ATTENDANCES FOR STUDENT.
                val url = "$CLASSES_URL/${c.id}/attendances"
                Log.i(TAG, "Requesting url: $url")
                val response = khttp.get(url, headers = mapOf("x-auth" to jwt))
                Log.i(TAG, "Response: $response")
                Log.i(TAG, "Response JSON array: ${response.jsonArray}")

                val attendancesJSONArray = response.jsonArray
                for (i in 0..(attendancesJSONArray.length() - 1)) {
                    val obj = attendancesJSONArray.getJSONObject(i)
                    val objStr = obj.toString()
                    val attendance = Gson().fromJson(objStr, Attendance::class.java)
                    Log.i(TAG, "Attendance: $attendance")
                    attendanceList.add(attendance)
                }
            }
            // LOADING ATTENDANCE CHECKS FOR STUDENT.
            for (attendance in attendanceList) {
                val url = "$ATTENDANCE_URL/${attendance.id}/checks"
                Log.i(TAG, "Requesting url: $url")
                val response = khttp.get(url, headers = mapOf("x-auth" to jwt))
                Log.i(TAG, "Response: $response")
                Log.i(TAG, "Response JSON array: ${response.jsonArray}")

                val attendanceChecksJSONArray = response.jsonArray
                for (i in 0..(attendanceChecksJSONArray.length() - 1)) {
                    val obj = attendanceChecksJSONArray.getJSONObject(i)
                    val objStr = obj.toString()
                    val attendanceCheck = Gson().fromJson(objStr, AttendanceCheck::class.java)
                    Log.i(TAG, "Attendance check: ${attendanceCheck.toString()}")
                    if (attendanceCheck.timestamp > System.currentTimeMillis()) {
                        attendanceCheckList.add(attendanceCheck)
                    }
                }
            }
            return attendanceCheckList
        }

        override fun onPostExecute(attendanceCheckList: ArrayList<AttendanceCheck>) {
            // ALARM MANAGER.
            alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // CREATING ALARM MANAGER SCHEDULES FOR STARTING CLASS NOTIFICATION AND TURNING ON WIFI.
            val sectionCourseTitle = preferences.getString(SECTION_COURSE_TITLE, "")
            Log.i(TAG, "Section course title $sectionCourseTitle")
            for (c in classList) {
                val intent = Intent(mContext, NextClassNotifier::class.java)
                intent.putExtra("startingClassTitle", sectionCourseTitle)
                val alarmId = "$CLASS_NOTIFICATION_PREFIX:${c.sectionId}:${c.id}"
                val alarmIdHashcode = alarmId.hashCode()
                val pendingIntent = PendingIntent.getBroadcast(mContext, alarmIdHashcode, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                val fiveMinBeforeStart = c.start - 2000 * 60
                alarmManager.setExact(AlarmManager.RTC, fiveMinBeforeStart, pendingIntent)
                Log.i(TAG, "Scheduled Starting Class Alarm at ${getDateTime(fiveMinBeforeStart)}")
            }

            // CREATING ALARM MANAGER SCHEDULES FOR TURNING OFF WIFI.
            for (c in classList) {
                val intent = Intent(mContext, NetworkDisabler::class.java)
                val alarmId = Random().nextInt(1000000)
                val pendingIntent = PendingIntent.getBroadcast(mContext, alarmId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                val endTime = c.end
                alarmManager.setExact(AlarmManager.RTC, endTime, pendingIntent)
                Log.i(TAG, "Scheduled Starting Class Alarm at ${getDateTime(endTime)}")
            }

            val wifiManager = mContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = false
            Log.i(TAG, "Disabled WiFi: ${wifiManager.isWifiEnabled}")

            val builder = Notification.Builder(mContext)
                    .setContentTitle("Attendance checks")
                    .setContentText("Successfully scheduled attendance checks for the next week")
                    .setSmallIcon(R.drawable.notification_icon_background)

            val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, builder.build())
        }
    }
}