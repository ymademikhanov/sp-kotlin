package com.example.android.simplealarmmanagerapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.fragments.SectionListFragment
import com.example.android.simplealarmmanagerapp.fragments.SetTargetBeaconFragment
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG = "HomeActivity"
    val STUDENT_URL = "https://attendance-app-dev.herokuapp.com/api/v1/students"

    lateinit var preferences: SharedPreferences

    lateinit var context: Context
    lateinit var sectionList: ArrayList<String>

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
        sectionList = ArrayList()
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
        val fragmentManager = getFragmentManager()
        when (item.itemId) {
            R.id.nav_courses -> {
                fragmentManager.beginTransaction().replace(R.id.content_frame, SectionListFragment()).commit()
            }
            R.id.nav_attendance -> {

            }
            R.id.set_beacon_target -> {
                fragmentManager.beginTransaction().replace(R.id.content_frame, SetTargetBeaconFragment()).commit()
            }
            R.id.nav_sign_out -> {
                val editor = preferences.edit()
                editor.remove("email")
                editor.remove("password")
                editor.remove("signedIn")
                editor.remove("jwt")
                editor.apply()
                finish()

                Log.i(TAG, "User signed out!")
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
