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
import com.example.android.simplealarmmanagerapp.StartingClassNotifier
import com.example.android.simplealarmmanagerapp.constants.*
import com.example.android.simplealarmmanagerapp.listview_adapters.ClassListViewAdapter
import com.example.android.simplealarmmanagerapp.listview_models.ClassListViewModel
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
    private var classTitleList: ArrayList<String> = ArrayList()
    private var attendanceList: ArrayList<Attendance> = ArrayList()
    private var classListViewItems: ArrayList<ClassListViewModel> = ArrayList()
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
            classTitleList.clear()
            val jwt = preferences.getString("jwt", "")
            val sectionId = sectionIds[0]
            val url = "$SECTIONS_URL/$sectionId/classes"
            Log.i(TAG, "Url: $url")
            val response = khttp.get(url, headers=mapOf("x-auth" to jwt))
            Log.i(TAG, "Response: $response")
            Log.i(TAG, "Response: ${response.jsonArray}")

            val classes = response.jsonArray
            for (i in 0..(classes.length() - 1)) {
                val objStr = classes.getJSONObject(i).toString()
                val universityClass = Gson().fromJson(objStr, Class::class.java)

                Log.i(TAG, "Class: $universityClass")

                val url = "$CLASSES_URL/${universityClass.id}/attendances"
                val response = khttp.get(url, headers= mapOf("x-auth" to jwt))

                Log.i(TAG, "Response for loading attendance for class is $response")

                val attendances = response.jsonArray
                val total = attendances.length()
                var score = 0
                for (i in 0..(attendances.length() - 1)) {
                    val objStr = attendances.getJSONObject(i).toString()
                    val attendance = Gson().fromJson(objStr, Attendance::class.java)
                    if (attendance.attended!!) {
                        score += 1
                    }
                }

                val classListViewTitle = "${getDateTime(universityClass.start)} - ${getDateTime(universityClass.end)}"
                val classListViewInfo = "$score out of $total attended"
                val classListViewModel = ClassListViewModel(classListViewTitle, classListViewInfo)

                classListViewItems.add(classListViewModel)
                classList.add(universityClass)
            }

            return response.jsonArray
        }

        override fun onPostExecute(classes: JSONArray) {
            var classListAdapter = ClassListViewAdapter(activity, R.layout.class_list_view_row, classListViewItems)
            classListView.adapter = classListAdapter
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

            // CREATING ALARM MANAGER SCHEDULES FOR STARTING CLASS NOTIFICATION.
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
            progressDialog.dismiss()
        }
    }
}