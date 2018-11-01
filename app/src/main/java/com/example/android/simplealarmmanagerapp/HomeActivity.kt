package com.example.android.simplealarmmanagerapp

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_header_home.*
import org.json.JSONArray

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG = "HomeActivity"

    val STUDENT_URL = "https://attendance-app-dev.herokuapp.com/api/v1/students"
    var SECTION_SUFFIX = "sections"

    val PREFERENCES_NAME = "AuthenticationPreferences"
    lateinit var preferences: SharedPreferences

    lateinit var context: Context
    lateinit var courseList: ArrayList<String>
    lateinit var courseListView : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        context = this
        preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        courseListView = findViewById(R.id.section_list_view)
        courseList = ArrayList()

        val studentId = preferences.getInt("accountId", 0)
        CourseListLoaderInBackground().execute(studentId)
    }

    inner class CourseListLoaderInBackground: AsyncTask<Int, String, JSONArray>() {
        override fun doInBackground(vararg studentIds: Int?): JSONArray {
            val studentId = studentIds[0]
            val response = khttp.get("$STUDENT_URL/$studentId/$SECTION_SUFFIX")
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

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_courses -> {
                // Handle the camera action
            }
            R.id.nav_attendance -> {

            }
            R.id.nav_sign_out -> {
                val editor = preferences.edit()
                editor.remove("email")
                editor.remove("password")
                editor.remove("signedIn")
                editor.apply()
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
