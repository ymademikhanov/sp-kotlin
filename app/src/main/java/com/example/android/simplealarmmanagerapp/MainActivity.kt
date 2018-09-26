package com.example.android.simplealarmmanagerapp

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
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
            val duration = et_duration.text.toString().toInt() * 1000
            val intent = Intent(context, BeaconScanner::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            ShowLog("Alarm was created for " + duration + " millis.")

            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + duration, pendingIntent)
        }
    }

    fun ShowLog(message: String) {
        Log.d(TAG, Date().toString() + message)
    }
}
