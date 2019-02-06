package com.example.android.simplealarmmanagerapp.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.android.simplealarmmanagerapp.services.AttendanceCheckService
import com.example.android.simplealarmmanagerapp.utilities.network.NetworkManager

class BeaconScanner : BroadcastReceiver() {
    val TAG = "BeaconScanner"
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "onReceive()")

        // Turning on Wifi.
        NetworkManager.enableNetwork(context!!)

        val intentService = Intent(context, AttendanceCheckService::class.java)
        val attendanceID = intent?.getIntExtra("attendanceId", 0)
        val attendanceCheckID = intent?.getIntExtra("attendanceCheckId", 0)

        Log.i(TAG, "Class ID: ${intent?.getIntExtra("classId", -15)}")

        intentService.putExtra("attendanceId", attendanceID)
        intentService.putExtra("attendanceCheckId", attendanceCheckID)

        context.startService(intentService)
    }
}