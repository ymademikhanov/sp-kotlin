package com.example.android.simplealarmmanagerapp.activities

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.fragments.SectionListFragment
import com.example.android.simplealarmmanagerapp.fragments.SetTargetBeaconFragment
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.preferences.removeUser
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    val TAG = "HomeActivity"

    lateinit var context: Context
    lateinit var preferences: SharedPreferences
    lateinit var sectionList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        context = this

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this,
                drawer_layout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close)

        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        preferences = context.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)
        sectionList = ArrayList()
    }

    override fun onBackPressed() {
        val fm = fragmentManager
        if (fm.backStackEntryCount > 0) {
            Log.i(TAG, "popping back stack")
            fm.popBackStack()
        } else {
            Log.i(TAG, "nothing on back stack, calling super")
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
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fragmentManager = getFragmentManager()
        when (item.itemId) {
            R.id.nav_courses -> {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, SectionListFragment()).commit()
            }
            R.id.nav_attendance -> {

            }
            R.id.set_beacon_target -> {
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, SetTargetBeaconFragment()).commit()
            }
            R.id.nav_sign_out -> {
                removeUser(preferences)
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
