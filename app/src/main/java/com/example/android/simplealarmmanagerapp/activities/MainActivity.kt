package com.example.android.simplealarmmanagerapp.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.android.simplealarmmanagerapp.R
import kotlinx.android.synthetic.main.activity_main.*
import androidx.navigation.ui.NavigationUI
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDaoLocal
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepository
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepositoryImpl

class MainActivity : AppCompatActivity(){

    lateinit var attCheckReportRepository: AttCheckReportRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navController = Navigation.findNavController(this, R.id.my_nav_host_fragment)

        setupBottomNavMenu(navController)
        setupSideNavigationMenu(navController)
        setupActionBar(navController)


        testSomeStuff()
    }


    fun testSomeStuff() {
        val localDao = AttCheckReportDaoLocal(this)
        attCheckReportRepository = AttCheckReportRepositoryImpl(null, localDao)

        val report = AttendanceCheckReport(10, 234, System.currentTimeMillis(), false, "Rustam is awesome")
        attCheckReportRepository.report(report)
    }

    private fun setupBottomNavMenu(navController: NavController) {
        bottomNavigationView2?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupSideNavigationMenu(navController: NavController) {
        sideNavigationView?.let {
            NavigationUI.setupWithNavController(it, navController)
        }
    }

    private fun setupActionBar(navController: NavController) {
        NavigationUI.setupActionBarWithNavController(this, navController, drawer_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val navController = Navigation.findNavController(this, R.id.my_nav_host_fragment)
        val navigated = NavigationUI.onNavDestinationSelected(item!!, navController)
        return navigated || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.my_nav_host_fragment)
        return NavigationUI.navigateUp(drawer_layout, navController)
    }
}