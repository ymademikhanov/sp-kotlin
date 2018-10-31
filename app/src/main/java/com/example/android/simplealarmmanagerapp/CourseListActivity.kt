package com.example.android.simplealarmmanagerapp

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.models.Account
import khttp.get
import khttp.post
import khttp.responses.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList

class CourseListActivity : AppCompatActivity() {
    val TAG = "CourseListActivity"
    val COURSES_URL = "https://attendance-app-dev.herokuapp.com/api/v1/courses"
    var studentId: Int = 0

    var context = this
    var areCoursesLoaded: Boolean = false

    lateinit var courseList: ArrayList<String>

    lateinit var courseListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        courseListView = findViewById(R.id.course_list_view)
        studentId = intent.getIntExtra("studentId", studentId)

        ShowLog("onCreate", studentId.toString())
        courseList = ArrayList<String>()
        CourseListLoaderInBackground().execute(studentId)
    }

    inner class CourseListLoaderInBackground: AsyncTask<Int, String, JSONArray>() {
        override fun doInBackground(vararg studentIds: Int?): JSONArray {
            val studentId = studentIds[0]
            return get(COURSES_URL).jsonArray
        }

        override fun onPostExecute(courses: JSONArray) {
            for (i in 0..(courses.length() - 1)) {
                courseList.add(courses.getJSONObject(i).getString("title"))
            }

            var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, courseList)
            courseListView.adapter = adapter
        }
    }

    fun ShowLog(subTag: String, message: String) {
        Log.d(TAG + ":" + subTag, Date().toString() + " : " + message)
    }
}