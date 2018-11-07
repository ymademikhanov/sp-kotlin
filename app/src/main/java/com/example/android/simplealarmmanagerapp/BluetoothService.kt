package com.example.android.simplealarmmanagerapp

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.IBinder
import android.util.Log
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.constants.TARGET_BEACON_ADDRESS_PREFERENCE_CONST
import java.util.*
import kotlin.concurrent.thread

class BluetoothService : Service() {

    var TAG = "BluetoothService"

    val mBluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var preferences: SharedPreferences

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        ShowLog("onCreate()")
        preferences = applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
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
            stopService(intent)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private val mReceiver = object : BroadcastReceiver() {

        var counter = 0

        fun createNotification(context: Context, deviceName: String, deviceHardwareAddress: String) {
            val builder = Notification.Builder(context)
                    .setContentTitle("Found a target beacon")
                    .setContentText("Address is $deviceHardwareAddress")
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

                    ShowLog("Found BT device with name: " + deviceName + " and address: " + deviceHardwareAddress)

                    var targetAddress = preferences.getString(TARGET_BEACON_ADDRESS_PREFERENCE_CONST, "")

                    val re = Regex("[^A-Za-z0-9 ]")
                    targetAddress = re.replace(targetAddress, "")
                    deviceName = re.replace(deviceName, "")

                    if (deviceName.equals(targetAddress)) {

                        createNotification(context, deviceName, deviceHardwareAddress)

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
