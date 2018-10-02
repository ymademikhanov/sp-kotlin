package com.example.android.simplealarmmanagerapp

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.concurrent.thread

class BluetoothService : Service() {

    var TAG = "BluetoothService"

    val mBluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ShowLog("onCreate()")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ShowLog("onStartCommand()")

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

        mBluetoothAdapter.enable()

        var i = 0
        while (!mBluetoothAdapter.isEnabled) {
            i += 1
        }

        mBluetoothAdapter.startDiscovery()
        registerReceiver(mReceiver, filter)

        thread() {
            Thread.sleep(12000)
            ShowLog("Cancelling discovery")
            while (mBluetoothAdapter.isDiscovering) {
                mBluetoothAdapter.cancelDiscovery()
            }
            ShowLog("Disabling bluetooth")
            mBluetoothAdapter.disable()

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancelAll()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private val mReceiver = object : BroadcastReceiver() {

        var counter = 0

        fun createNotification(context: Context, deviceName: String, deviceHardwareAddress: String) {
            val builder = Notification.Builder(context)
                    .setContentTitle("Name: " + deviceName)
                    .setContentText("Address: " + deviceHardwareAddress)
                    .setSmallIcon(R.drawable.notification_icon_background)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(counter, builder.build())
            counter += 1
            ShowLog("Counter " + counter)
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action

            ShowLog("onReceive in Bluetooth signal receiver")
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    var device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var deviceName = device.name
                    var deviceHardwareAddress = device.address // MAC address

                    if (deviceName == null) {
                        deviceName = "something"
                    }
                    createNotification(context, deviceName, deviceHardwareAddress)

                    ShowLog("Found BT device with name: " + deviceName + " and address: " + deviceHardwareAddress)

                    if (deviceName.equals("ymademikhanov")) {
                        ShowLog("CHECKIN IS DONE!")
                        ShowLog("Cancelling discovery")
                        while (mBluetoothAdapter.isDiscovering) {
                            mBluetoothAdapter.cancelDiscovery()
                        }
                        ShowLog("Disabling bluetooth")
                        mBluetoothAdapter.disable()
                    }

                }
            }
        }
    }

    fun ShowLog(message: String) {
        Log.w(TAG, Date().toString() + " : " + message)
    }
}
