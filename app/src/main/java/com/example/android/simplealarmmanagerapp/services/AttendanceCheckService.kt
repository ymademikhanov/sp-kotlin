package com.example.android.simplealarmmanagerapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.BAC_PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.utilities.constants.ATTENDANCE_URL
import com.example.android.simplealarmmanagerapp.utilities.constants.TARGET_BEACON_ADDRESS_CONST
import khttp.patch
import khttp.responses.Response
import org.json.JSONObject
import kotlin.concurrent.thread


class AttendanceCheckService : Service() {
    var TAG = "AttendanceCheckService"

    val ATTENDANCE_CHECK_DURATION_MILLIS: Long = 20000

    lateinit var preferences: SharedPreferences
    lateinit var targetAddress : String

    val mBluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var attendanceId : Int = 0
    var attendanceCheckId : Int = 0

    override fun onCreate() {
        Log.i(TAG, "onCreate()")
        preferences =
                applicationContext.getSharedPreferences(BAC_PREFERENCES_NAME, Context.MODE_PRIVATE)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")
        targetAddress = preferences.getString(TARGET_BEACON_ADDRESS_CONST, "")

        attendanceId = intent!!.getIntExtra("attendanceId", 0)
        attendanceCheckId = intent.getIntExtra("attendanceCheckId", 0)

        // Logging.
        Log.i(TAG, "Attendance ID: $attendanceId")
        Log.i(TAG, "Attendance Check ID: $attendanceCheckId")

        enableBluetoothAndStartDiscovery()

        thread {
            Thread.sleep(ATTENDANCE_CHECK_DURATION_MILLIS)
            stopDiscoverAndDisableBluetooth()
            stopService(intent)
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    private val mReceiver = object : BroadcastReceiver() {

        var counter = 0

        @SuppressLint("PrivateResource")
        fun createNotification(context: Context, deviceName: String, deviceHardwareAddress: String) {
            val builder = Notification.Builder(context)
                    .setContentTitle("Found a target beacon")
                    .setContentText("Address is $deviceHardwareAddress")
                    .setSmallIcon(R.drawable.notification_icon_background)

            val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(counter, builder.build())
            counter += 1
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action!!

            Log.i(TAG, "onReceive in Bluetooth signal receiver")

            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    var device: BluetoothDevice =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    var deviceName = device.name
                    var deviceHardwareAddress = device.address // MAC address

                    if (deviceName == null) {
                        deviceName = "something"
                    }

                    Log.i(TAG, "Found a BT device with address = $deviceHardwareAddress")

                    val re = Regex("[^A-Za-z0-9 ]")
                    val target = re.replace(targetAddress, "")
                    val current = re.replace(deviceHardwareAddress, "")

                    if (current == target) {
                        AttendanceReporter().execute(attendanceId)
                        createNotification(context, deviceName, deviceHardwareAddress)
                        stopDiscoverAndDisableBluetooth()
                    }
                }
            }
        }
    }

    private fun enableBluetoothAndStartDiscovery() {
        Log.i(TAG, "Enabling BT discovery")
        mBluetoothAdapter.enable()
        var i = 0
        while (!mBluetoothAdapter.isEnabled) {
            i += 1
        }
        mBluetoothAdapter.startDiscovery()
        Log.i(TAG, "BT discovery started")

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        Log.i(TAG, "Registered receiver")
    }

    fun stopDiscoverAndDisableBluetooth() {
        Log.i(TAG, "Disabling BT discovery")
        while (mBluetoothAdapter.isDiscovering) {
            mBluetoothAdapter.cancelDiscovery()
        }
        Log.i(TAG, "Disabling Bluetooth")
        mBluetoothAdapter.disable()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    inner class AttendanceReporter: AsyncTask<Int, String, Response>() {
        override fun doInBackground(vararg args: Int?): Response {
            Log.i(TAG, "Started sending attendance check")
            val attendanceId = args[0]
            val checkTime = System.currentTimeMillis()
            val data = mapOf("attendance_id" to attendanceId, "checked" to true, "timestamp" to checkTime)
            val jwt = preferences.getString("jwt", "")

            Log.i(TAG, "Attendance check data: $data")
            Log.i(TAG, "JWT token: $jwt")

            val url = "$ATTENDANCE_URL/checks/$attendanceCheckId"

            Log.i(TAG, "Url to send report $url")
            val response = patch(url, data= JSONObject(data), headers = mapOf("x-auth" to jwt))

            Log.i(TAG, "Attendance Report Response: $response")
            return response
        }

        override fun onPostExecute(response: Response) {
            if (response.statusCode == 200) {
                Log.i(TAG, "Successful attendance report")
            } else {
                Log.i(TAG, "Attendance Reporter Error Content: ${response.content}")
                Log.i(TAG, "Attendance Reporter Error Raw: ${response.raw}")
            }
        }
    }
}
