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
import com.example.android.simplealarmmanagerapp.listview_adapters.ClassListViewAdapter
import com.example.android.simplealarmmanagerapp.listview_models.ClassListViewModel
import com.example.android.simplealarmmanagerapp.models.Attendance
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.Class
import com.example.android.simplealarmmanagerapp.utils.getDateTime
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
    private var classListViewItems: ArrayList<ClassListViewModel> = ArrayList()

    lateinit var fragmentView: View
    lateinit var classListView : ListView
    lateinit var progressDialog: ProgressDialog

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

        if (verifyAvailableNetwork()) {
            ClassListLoaderInBackground().execute(sectionId)
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
}