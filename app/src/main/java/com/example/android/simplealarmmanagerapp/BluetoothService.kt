package com.example.android.simplealarmmanagerapp

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import com.example.android.simplealarmmanagerapp.constants.ATTENDANCE_URL
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.constants.TARGET_BEACON_ADDRESS_PREFERENCE_CONST
import khttp.post
import khttp.responses.Response
//import org.mapdb.DBMaker
//import org.mapdb.Serializer
import kotlin.concurrent.thread

class BluetoothService : Service() {

    var TAG = "BluetoothService"

    val mBluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    lateinit var preferences: SharedPreferences

    var classId : Int = 0
    var studentId : Int = 0
    lateinit var targetAddress : String

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate()")
        preferences = applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand()")

        studentId = preferences.getInt("accountId", 0)
        targetAddress = preferences.getString(TARGET_BEACON_ADDRESS_PREFERENCE_CONST, "")

//        val checkTime = System.currentTimeMillis() / 1000
//        var db = DBMaker.fileDB("/some/file").make()
//
//        var map = db.hashMap("collectionName", Serializer.INTEGER, Serializer.STRING).createOrOpen()
//        classId = map.get(checkTime.toInt())!!.toInt()

        enableBluetoothAndStartDiscovery()

        thread() {
            Thread.sleep(20000)
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

        fun createNotification(context: Context, deviceName: String, deviceHardwareAddress: String) {
            val builder = Notification.Builder(context)
                    .setContentTitle("Found a target beacon")
                    .setContentText("Address is $deviceHardwareAddress")
                    .setSmallIcon(R.drawable.notification_icon_background)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(counter, builder.build())
            counter += 1
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action

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

                    if (current.equals(target)) {

                        createNotification(context, deviceName, deviceHardwareAddress)

                        AttendanceReporter().execute(studentId, classId)

                        stopDiscoverAndDisableBluetooth()
                    }
                }
            }
        }
    }

    fun enableBluetoothAndStartDiscovery() {
        Log.i(TAG, "Started enabling BT discovery")
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
        Log.i(TAG, "Started disabling BT discovery")
        mBluetoothAdapter.cancelDiscovery()
        var i = 0
        while (mBluetoothAdapter.isDiscovering) {
            i += 1
        }
        Log.i(TAG, "Disabled BT discovery")
        mBluetoothAdapter.disable()
    }

    inner class AttendanceReporter: AsyncTask<Int, String, Response>() {
        override fun doInBackground(vararg args: Int?): Response {
            Log.i(TAG, "Started sending attendance check")
            val studentId = args[0]
            val classId = args[1]
            val data = mapOf("student_id" to studentId, "class_id" to classId)
            val jwt = preferences.getString("jwt", "")

            Log.i(TAG, "Attendance check data: $data")

            val response = post(ATTENDANCE_URL, data=data, headers=mapOf("x-auth" to jwt))

            Log.i(TAG, "Attendance Report Response: $response")
            return response
        }

        override fun onPostExecute(response: Response) {
            if (response.statusCode == 200) {
                Log.i(TAG, "Successful attendance report")
            } else {
                Log.i(TAG, "Error: ${response.content}")
                Log.i(TAG, "Error: ${response.raw}")
            }
        }
    }
}
