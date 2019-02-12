package com.example.android.simplealarmmanagerapp.utilities.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.util.Log

class NetworkManager {
    companion object {
        val TAG = "NetworkManager"

        fun enableNetwork(context: Context) {
            val wifiManager = context
                    .applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            while (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
            Log.i(TAG, "Enabled WiFi: ${wifiManager.isWifiEnabled}")
        }

        fun disableNetwork(context: Context) {
            val wifiManager = context.
                    applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.isWifiEnabled = false
            Log.i(TAG, "Disabled WiFi: ${wifiManager.isWifiEnabled}")
        }

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }
}