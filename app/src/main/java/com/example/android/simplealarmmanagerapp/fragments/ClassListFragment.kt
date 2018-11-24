package com.example.android.simplealarmmanagerapp.fragments

import android.app.AlarmManager
import android.app.Fragment
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.BeaconScanner
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.constants.*
import com.example.android.simplealarmmanagerapp.models.Attendance
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.Class
import com.google.gson.Gson
import org.json.JSONArray
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class ClassListFragment : Fragment() {
    val TAG = "ClassListActivity"

    private lateinit var preferences: SharedPreferences
    private lateinit var preferencesTimeToClass: SharedPreferences
    private var classList: ArrayList<Class> = ArrayList()
    private var attendanceList: ArrayList<Attendance> = ArrayList()
    private var attendanceCheckList: ArrayList<AttendanceCheck> = ArrayList()

    lateinit var fragmentView: View
    lateinit var alarmManager : AlarmManager
    lateinit var classListView : ListView
    lateinit var progressDialog: ProgressDialog
    lateinit var scheduleBTAutoChecksButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.activity_class_list, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onCreate()")

        progressDialog = ProgressDialog(activity)
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val sectionId = preferences.getInt(SECTION_ID_EXTRA, 0)
        Log.i(TAG, "Section ID is $sectionId")

        preferencesTimeToClass = activity.getSharedPreferences(TIME_TO_CLASS_ID, Context.MODE_PRIVATE)

        classListView = view.findViewById(R.id.class_list_view)

        scheduleBTAutoChecksButton = view.findViewById(R.id.schedule_bt_auto_checks_btn)
        scheduleBTAutoChecksButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i(TAG, "Scheduling attendance checks")
                LoadAndScheduleAttendanceInBackground().execute(sectionId)
            }
        })
        ClassListLoaderInBackground().execute(sectionId)
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

    inner class ClassListLoaderInBackground: AsyncTask<Int?, String, JSONArray>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Loading classes ...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg sectionIds: Int?): JSONArray {
            classList.clear()
            val jwt = preferences.getString("jwt", "")
            val sectionId = sectionIds[0]
            val url = "$SECTIONS_URL/$sectionId/classes"
            Log.i(TAG, "Url: $url")
            val response = khttp.get(url, headers=mapOf("x-auth" to jwt))
            Log.i(TAG, "Response: $response")
            Log.i(TAG, "Response: ${response.jsonArray}")
            return response.jsonArray
        }

        override fun onPostExecute(classes: JSONArray) {
            for (i in 0..(classes.length() - 1)) {
                val obj = classes.getJSONObject(i)
                val objStr = obj.toString()
                val universityClass = Gson().fromJson(objStr, Class::class.java)
                Log.i(TAG, "Class: $universityClass")
                classList.add(universityClass)
            }

            var adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, classList)
            classListView.adapter = adapter

            progressDialog.dismiss()
        }
    }

    inner class LoadAndScheduleAttendanceInBackground: AsyncTask<Int?, String, ArrayList<AttendanceCheck>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Loading attendances ...")
            progressDialog.setCancelable(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg sectionIds: Int?): ArrayList<AttendanceCheck> {
            classList.clear()
            val jwt = preferences.getString("jwt", "")
            val sectionId = sectionIds[0]
            val url = "$SECTIONS_URL/$sectionId/classes"

            Log.i(TAG, "Url: $url")

            val response = khttp.get(url, headers=mapOf("x-auth" to jwt))

            Log.i(TAG, "Response: $response")
            Log.i(TAG, "Response: ${response.jsonArray}")

            val classesJSONArray = response.jsonArray
            for (i in 0..(classesJSONArray.length() - 1)) {
                val obj = classesJSONArray.getJSONObject(i)
                val objStr = obj.toString()
                val universityClass = Gson().fromJson(objStr, Class::class.java)
                Log.i(TAG, "Class: $universityClass")
                classList.add(universityClass)
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
                    Log.i(TAG, "Attendance check: $attendanceCheck")
                    attendanceCheckList.add(attendanceCheck)
                }
            }
            return attendanceCheckList
        }

        override fun onPostExecute(attendanceCheckList: ArrayList<AttendanceCheck>) {
            // CREATING ALARM MANAGER SCHEDULES FOR ATTENDANCE CHECKS.
            alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (attendanceCheck in attendanceCheckList) {
                val intent = Intent(activity, BeaconScanner::class.java)
                intent.putExtra("attendanceId", attendanceCheck.attendanceId)
                intent.putExtra("attendanceCheckId", attendanceCheck.id)
                val alarmId = Random().nextInt(1000000)
                val pendingIntent = PendingIntent.getBroadcast(activity, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                alarmManager.setExact(AlarmManager.RTC, attendanceCheck.timestamp, pendingIntent)
                Log.i(TAG, "Scheduled Alarm at ${getDateTime(attendanceCheck.timestamp)}")
            }
            progressDialog.dismiss()
        }
    }
}