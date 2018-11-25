package com.example.android.simplealarmmanagerapp.fragments

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.android.simplealarmmanagerapp.BeaconScanner
import com.example.android.simplealarmmanagerapp.DisableWifi
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.StartingClassNotifier
import com.example.android.simplealarmmanagerapp.constants.*
import com.example.android.simplealarmmanagerapp.models.Attendance
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.models.Section
import com.google.gson.Gson
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class SectionListFragment : Fragment() {
    val TAG = "SectionListFragment"

    private var sectionList: ArrayList<Section> = ArrayList()
    private var sectionTitleList: ArrayList<String> = ArrayList()

    lateinit var preferences: SharedPreferences
    lateinit var fragmentView: View
    lateinit var sectionListView : ListView
    lateinit var progressDialog: ProgressDialog
    lateinit var scheduleBTAutoChecksButton: Button
    private var attendanceList: ArrayList<Attendance> = ArrayList()
    private var attendanceCheckList: ArrayList<AttendanceCheck> = ArrayList()

    lateinit var alarmManager : AlarmManager
    var classList: ArrayList<Class> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.section_list_layout, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(activity)

        scheduleBTAutoChecksButton = view.findViewById(R.id.schedule_bt_auto_checks_btn)
        scheduleBTAutoChecksButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i(TAG, "Scheduling attendance checks")
                if (verifyAvailableNetwork()) {
                    LoadAndScheduleAttendanceInBackground().execute(sectionList)
                } else {
                    Toast.makeText(activity.applicationContext, "No internet connectivity =(", Toast.LENGTH_SHORT).show()
                }
            }
        })

        sectionListView = view.findViewById(R.id.section_list_view)
        sectionListView.setOnItemClickListener { _, _, position, _ ->
            val editor = preferences.edit()
            editor.putInt(SECTION_ID_EXTRA, sectionList[position].id!!)
            editor.putString(SECTION_COURSE_TITLE, sectionList[position].course?.title)
            editor.apply()

            val fr = ClassListFragment()
            val fragmentManager = fragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, fr)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val jwt = preferences.getString("jwt", "")
        Log.i(TAG, "Started loading sections by jwt $jwt")

        if (verifyAvailableNetwork()) {
            progressDialog.setMessage("Loading sections ...")
            progressDialog.setCancelable(false)
            progressDialog.show()
            SectionListLoaderInBackground().execute(jwt)
        } else {
            Toast.makeText(activity.applicationContext, "No internet connectivity =(", Toast.LENGTH_SHORT).show()
        }
    }

    fun verifyAvailableNetwork() : Boolean {
        val connectivityManager = activity.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

    private fun getDateTime(t: Long): String {
        val stamp = Timestamp(t)
        val date = Date(stamp.getTime())
        return date.toString()
    }

    inner class SectionListLoaderInBackground: AsyncTask<String, String, JSONArray>() {
        override fun doInBackground(vararg jwts: String): JSONArray {
            sectionTitleList.clear()
            sectionList.clear()
            val jwt = jwts[0]
            val response = khttp.get(MY_SECTION_URL, headers=mapOf("x-auth" to jwt))
            Log.i(TAG, "Response: ${response.jsonArray}")
            return response.jsonArray
        }

        override fun onPostExecute(sections: JSONArray) {
            for (i in 0..(sections.length() - 1)) {
                val obj = sections.getJSONObject(i)
                val objectJSONString = obj.toString()
                val section = Gson().fromJson(objectJSONString, Section::class.java)
                Log.i(TAG, "Object JSON: $objectJSONString")
                Log.i(TAG, "Section: $section")
                sectionList.add(section)
                sectionTitleList.add(section.course!!.title)
            }
            var adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, sectionTitleList)
            sectionListView.adapter = adapter
            progressDialog.dismiss()
        }
    }

    inner class LoadAndScheduleAttendanceInBackground: AsyncTask<ArrayList<Section>, String, ArrayList<AttendanceCheck>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            classList.clear()
            progressDialog.setMessage("Loading attendances ...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg sections_vars: ArrayList<Section>?): ArrayList<AttendanceCheck> {
            val jwt = preferences.getString("jwt", "")
            val sections = sections_vars[0]

            // LOADING COURSE SECTION CLASSES FOR STUDENT.
            for (section in sections!!) {
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
                    classList.add(universityClass)
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
            alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // CREATING ALARM MANAGER SCHEDULES FOR STARTING CLASS NOTIFICATION AND TURNING ON WIFI.
            val sectionCourseTitle = preferences.getString(SECTION_COURSE_TITLE, "")
            Log.i(TAG, "Section course title $sectionCourseTitle")
            for (c in classList) {
                val intent = Intent(activity, StartingClassNotifier::class.java)
                intent.putExtra("startingClassTitle", sectionCourseTitle)
                val alarmId = Random().nextInt(1000000)
                val pendingIntent = PendingIntent.getBroadcast(activity, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val five_mins_before_start = c.start - 2000 * 60
                alarmManager.setExact(AlarmManager.RTC, five_mins_before_start, pendingIntent)
                Log.i(TAG, "Scheduled Starting Class Alarm at ${getDateTime(five_mins_before_start)}")
            }

            // CREATING ALARM MANAGER SCHEDULES FOR ATTENDANCE CHECKS.
            for (attendanceCheck in attendanceCheckList) {
                val intent = Intent(activity, BeaconScanner::class.java)
                intent.putExtra("attendanceId", attendanceCheck.attendanceId)
                intent.putExtra("attendanceCheckId", attendanceCheck.id)
                val alarmId = Random().nextInt(1000000)
                val pendingIntent = PendingIntent.getBroadcast(activity, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager.setExact(AlarmManager.RTC, attendanceCheck.timestamp, pendingIntent)
                Log.i(TAG, "Scheduled Alarm at ${getDateTime(attendanceCheck.timestamp)}")
            }

            // CREATING ALARM MANAGER SCHEDULES FOR TURNING OFF WIFI.
            for (c in classList) {
                val intent = Intent(activity, DisableWifi::class.java)
                val alarmId = Random().nextInt(1000000)
                val pendingIntent = PendingIntent.getBroadcast(activity, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                val endTime = c.end
                alarmManager.setExact(AlarmManager.RTC, endTime, pendingIntent)
                Log.i(TAG, "Scheduled Starting Class Alarm at ${getDateTime(endTime)}")
            }

            progressDialog.dismiss()
        }
    }
}