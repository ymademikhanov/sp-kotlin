package com.example.android.simplealarmmanagerapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.simplealarmmanagerapp.services.AttendanceCheckService

class AttendanceCheckBR : BroadcastReceiver() {
    val TAG = "AttendanceCheckBR"

    override fun onReceive(context: Context?, intent: Intent?) {
        // Logging.
        Log.i(TAG, "onReceive()")

        // Enabling WiFi.
//        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        wifiManager.isWifiEnabled = true
//        Log.i(TAG, "Enabled WiFi: ${wifiManager.isWifiEnabled}")

        val intentService = Intent(context, AttendanceCheckService::class.java)

        // Logging the class id.
        Log.i(TAG, "Class Id: ${intent!!.getIntExtra("classId", 0)}")

        intentService.putExtra("attendanceId", intent.getIntExtra("attendanceId", 0))
        intentService.putExtra("attendanceCheckId", intent.getIntExtra("attendanceCheckId", 0))

        context?.startService(intentService)
    }
}

