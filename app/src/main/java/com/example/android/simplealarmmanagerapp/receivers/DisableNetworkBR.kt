package com.example.android.simplealarmmanagerapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class DisableNetworkBR : BroadcastReceiver() {
    val TAG = "DisableNetworkBR"

    override fun onReceive(context: Context?, intent: Intent?) {
        // Logging.
        Log.i(TAG, "onReceive()")


        val wifiManager =
                context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Disabling WiFi in blocking manner.
        // Wifi is not turned off immediately.
        while (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }
}

