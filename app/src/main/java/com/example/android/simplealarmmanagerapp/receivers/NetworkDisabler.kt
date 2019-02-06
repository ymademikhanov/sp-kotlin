package com.example.android.simplealarmmanagerapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class NetworkDisabler : BroadcastReceiver() {
    val TAG = "NetworkDisabler"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        // Disabling WiFi.
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        while (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }
}