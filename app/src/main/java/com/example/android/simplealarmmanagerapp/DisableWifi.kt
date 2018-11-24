package com.example.android.simplealarmmanagerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class DisableWifi : BroadcastReceiver() {
    val TAG = "DisableWifi"


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        // Disabling WiFi.
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        while (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }
}

