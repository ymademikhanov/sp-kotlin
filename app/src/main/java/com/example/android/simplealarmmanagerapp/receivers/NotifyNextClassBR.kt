package com.example.android.simplealarmmanagerapp.receivers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.util.Log
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.STARTING_CLASS_MESSAGE

class NotifyNextClassBR : BroadcastReceiver() {
    val TAG = "NotifyNextClassBR"

    @SuppressLint("PrivateResource")
    override fun onReceive(context: Context?, intent: Intent?) {
        // Logging.
        Log.i(TAG, "onReceive()")

        // Enabling WiFi.
        val wifiManager =
                context?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManager.isWifiEnabled = true

        // Logging whether Wifi is enabled.
        Log.i(TAG, "Enabled WiFi: ${wifiManager.isWifiEnabled}")

        // Creating a notification for a starting class.
        // Extracting class title from intent extras.
        val startingClassTitle = intent?.getStringExtra("startingClassTitle")
        val builder = Notification.Builder(context)
                .setContentTitle(startingClassTitle)
                .setContentText(STARTING_CLASS_MESSAGE)
                .setSmallIcon(R.drawable.notification_icon_background)

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Currently the notification id is not important.
        notificationManager.notify(0, builder.build())
    }
}

