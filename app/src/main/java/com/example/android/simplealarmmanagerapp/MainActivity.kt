package com.example.android.simplealarmmanagerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.max
import java.util.*


class MainActivity : AppCompatActivity() {

    var TAG = "MainActivity"

    lateinit var context : Context
    lateinit var alarmManager : AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        btn_create.setOnClickListener {
            val start_within = et_start_within.text.toString().toInt() * 1000
            val interval = max(et_interval.text.toString().toInt(), 15) * 1000
            var checks_number = max(et_count.text.toString().toInt(), 1)

            val offset = System.currentTimeMillis()
            var start_time = start_within
            while (checks_number > 0) {

                val intent = Intent(context, BeaconScanner::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, checks_number, intent, PendingIntent.FLAG_UPDATE_CURRENT)
                ShowLog("Alarm was created for " + start_time + " millis.")
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,offset + start_time, pendingIntent)

                start_time += interval
                checks_number -= 1
            }
        }
    }

    fun ShowLog(message: String) {
        Log.w(TAG, Date().toString() + message)
    }
}
