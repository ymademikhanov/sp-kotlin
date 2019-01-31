package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.STUDENT_URL
import khttp.get
import org.json.JSONArray
import kotlin.collections.ArrayList

class CourseListActivity : AppCompatActivity() {
    val TAG = "CourseListActivity"
    var SECTION_SUFFIX = "sections"
    val PREFERENCES_NAME = "AuthenticationPreferences"

    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var courseList: ArrayList<String>
    lateinit var courseListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)

        context = this
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        courseListView = findViewById(R.id.course_list_view)
        courseList = ArrayList()

        val studentId = preferences.getInt("accountId", 0)
        CourseListLoaderInBackground().execute(studentId)
    }

    inner class CourseListLoaderInBackground: AsyncTask<Int, String, JSONArray>() {
        override fun doInBackground(vararg studentIds: Int?): JSONArray {
            val studentId = studentIds[0]
            val response = get("$STUDENT_URL/$studentId/$SECTION_SUFFIX")
            Log.i(TAG, "Response: $response")
            return response.jsonArray
        }

        override fun onPostExecute(courses: JSONArray) {
            for (i in 0..(courses.length() - 1)) {
                courseList.add(courses.getJSONObject(i).getString("title"))
            }

            var adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, courseList)
            courseListView.adapter = adapter
        }
    }
}