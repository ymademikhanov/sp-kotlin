package com.example.android.simplealarmmanagerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class BeaconScanner : BroadcastReceiver() {
    val TAG = "BeaconScanner"
    override fun onReceive(context: Context?, intent: Intent?) {
        ShowLog("onReceive()")

        val intentService = Intent(context, BluetoothService::class.java)
        context?.startService(intentService)
    }

    fun ShowLog(message: String) {
        Log.w(TAG, Date().toString() + message)
    }
}

