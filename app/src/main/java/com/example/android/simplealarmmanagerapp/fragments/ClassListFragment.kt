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
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.constants.SECTIONS_URL
import com.example.android.simplealarmmanagerapp.constants.SECTION_ID_EXTRA
import com.example.android.simplealarmmanagerapp.constants.TARGET_BEACON_ADDRESS_PREFERENCE_CONST
import com.example.android.simplealarmmanagerapp.models.Class
import com.google.gson.Gson
import org.json.JSONArray
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class ClassListFragment : Fragment() {
    lateinit var fragmentView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.activity_class_list, container, false)
        return fragmentView
    }

    val TAG = "ClassListActivity"

    private lateinit var preferences: SharedPreferences

    lateinit var alarmManager : AlarmManager
    lateinit var classListView : ListView
    lateinit var progressDialog: ProgressDialog
    lateinit var scheduleBTAutoChecksButton: Button
    var classList: ArrayList<Class> = ArrayList()


    fun findNextClassTime(t: Long) : Long {
        var classTime = t
        val currentTime = System.currentTimeMillis()
        val weekMillis = 604800 * 1000
        while (classTime < currentTime) {
            classTime += weekMillis
        }
        return classTime
    }

    private fun getDateTime(t: Long): String {
        val stamp = Timestamp(t)
        val date = Date(stamp.getTime())
        return date.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(TAG, "onCreate()")

        progressDialog = ProgressDialog(activity)
        preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        classListView = view.findViewById(R.id.class_list_view)
        alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        scheduleBTAutoChecksButton = view.findViewById(R.id.schedule_bt_auto_checks_btn)
        scheduleBTAutoChecksButton.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Log.i(TAG, "Scheduling ...")

                for (c in classList) {
                    Log.i(TAG, "The class with id ${c.id} and section id ${c.sectionId}")

                    var nextStartTime = findNextClassTime(c.start)
                    var nextEndTime = findNextClassTime(c.end)
                    var interval = (nextEndTime - nextStartTime) / c.checks!!

                    Log.i(TAG, "The next start time is ${getDateTime(nextStartTime)}")
                    Log.i(TAG, "The next end time is ${getDateTime(nextEndTime)}")

                    while (nextStartTime < nextEndTime) {
                        val intent = Intent(activity, BeaconScanner::class.java)
                        val pendingIntent = PendingIntent.getBroadcast(activity, Random().nextInt(1000000), intent, PendingIntent.FLAG_ONE_SHOT)
                        alarmManager.setExact(AlarmManager.RTC,nextStartTime, pendingIntent)
                        Log.i(TAG, "The intermediate check is ${getDateTime(nextStartTime)}")
                        nextStartTime += interval
                    }
                }
            }
        })

        val sectionId = preferences.getInt(SECTION_ID_EXTRA, 0)
        Log.i(TAG, "Section ID is $sectionId")

        progressDialog.setMessage("Loading classes ...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        ClassListLoaderInBackground().execute(sectionId)
    }

    inner class ClassListLoaderInBackground: AsyncTask<Int?, String, JSONArray>() {
        override fun doInBackground(vararg sectionIds: Int?): JSONArray {
            classList.clear()

            val jwt = preferences.getString("jwt", "")
            val sectionId = sectionIds[0]
            val url = "$SECTIONS_URL/$sectionId/classes"

            Log.i(TAG, "Url: $url")

            val response = khttp.get(url, headers=mapOf("x-auth" to jwt))

            Log.i(TAG, "Response: ${response}")

            Log.i(TAG, "Response: ${response.jsonArray}")

            return response.jsonArray
        }

        override fun onPostExecute(classes: JSONArray) {
            for (i in 0..(classes.length() - 1)) {
                val obj = classes.getJSONObject(i)
                val objStr = obj.toString()
                val universityClass = Gson().fromJson(objStr, Class::class.java)
                Log.i(TAG, "Class2: $universityClass")
                classList.add(universityClass)
            }

            var adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, classList)
            classListView.adapter = adapter

            progressDialog.hide()
        }
    }

}