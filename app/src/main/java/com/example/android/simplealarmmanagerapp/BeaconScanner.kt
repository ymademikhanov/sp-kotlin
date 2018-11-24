package com.example.android.simplealarmmanagerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import java.util.*

class BeaconScanner : BroadcastReceiver() {
    val TAG = "BeaconScanner"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        // Enabling WiFi.
//        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        wifiManager.isWifiEnabled = true
//        Log.i(TAG, "Enabled WiFi: ${wifiManager.isWifiEnabled}")

        val intentService = Intent(context, BluetoothService::class.java)
        Log.i(TAG, "Class Id: ${intent!!.getIntExtra("classId", 0)}")
        intentService.putExtra("attendanceId", intent.getIntExtra("attendanceId", 0))
        intentService.putExtra("attendanceCheckId", intent.getIntExtra("attendanceCheckId", 0))
        context?.startService(intentService)
    }
}

