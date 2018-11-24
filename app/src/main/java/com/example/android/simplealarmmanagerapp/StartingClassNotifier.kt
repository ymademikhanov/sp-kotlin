package com.example.android.simplealarmmanagerapp

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log

class StartingClassNotifier : BroadcastReceiver() {
    val TAG = "StartingClassNotifier"


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        // Enabling WiFi.
        val wifiManager = context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true
        Log.i(TAG, "Enabled WiFi: ${wifiManager.isWifiEnabled}")


        // Starting notification.
        val startingClassTitle = intent?.getStringExtra("startingClassTitle")
        val builder = Notification.Builder(context)
                .setContentTitle("$startingClassTitle")
                .setContentText("is going to start soon...")
                .setSmallIcon(R.drawable.notification_icon_background)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}

