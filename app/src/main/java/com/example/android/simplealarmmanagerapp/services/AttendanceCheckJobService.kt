package com.example.android.simplealarmmanagerapp.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.*
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.models.AttendanceCheck
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDaoLocal
import com.example.android.simplealarmmanagerapp.models.daos.AttCheckReportDaoRemote
import com.example.android.simplealarmmanagerapp.models.entities.AttendanceCheckReport
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepository
import com.example.android.simplealarmmanagerapp.models.repositories.AttCheckReportRepositoryImpl
import com.example.android.simplealarmmanagerapp.utilities.constants.BAC_PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.utilities.constants.TARGET_BEACON_ADDRESS_CONST
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPIClient
import kotlin.concurrent.thread

class AttendanceCheckJobService: JobIntentService() {

    val TAG = "AttCheckJobService"

    val ATTENDANCE_CHECK_DURATION_MILLIS: Long = 20000
    lateinit var attCheckReportRepository: AttCheckReportRepository

    lateinit var preferences: SharedPreferences
    lateinit var targetAddress : String

    val mBluetoothAdapter : BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    var attendanceId : Int = 0
    var attendanceCheckId : Int = 0

    companion object {
        val JOB_ID = 1000

        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, AttendanceCheckJobService::class.java, JOB_ID, work)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate()")
        preferences =
                applicationContext.getSharedPreferences(BAC_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onHandleWork(intent: Intent) {
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
    }

    private val mReceiver = object : BroadcastReceiver() {

        var counter = 0

        @SuppressLint("PrivateResource")
        fun createNotification(context: Context, deviceName: String, deviceAddress: String) {
            val builder = Notification.Builder(context)
                    .setContentTitle("Found a beacon")
                    .setContentText("Address is $deviceAddress")
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
                    val target = re.replace("5C-F9-38-93-4E-3F", "")
                    val current = re.replace(deviceHardwareAddress.toUpperCase(), "")

                    createNotification(context, deviceName, deviceHardwareAddress)

                    if (current == target) {
                        val timestamp = System.currentTimeMillis()

                        // Reporting successful check.
                        val jwt = preferences.getString("jwt", "")
                        val jwtMap = mapOf("x-auth" to jwt)

                        val localDao = AttCheckReportDaoLocal(applicationContext)
                        val remoteDao = AttCheckReportDaoRemote(jwtMap, StudentAPIClient.client)

                        attCheckReportRepository = AttCheckReportRepositoryImpl(remoteDao, localDao)
                        val check = AttendanceCheck(attendanceCheckId, attendanceId, timestamp, true)
                        attCheckReportRepository.report(check)

//                        AttendanceReporter().execute(attendanceId)
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
        if (mBluetoothAdapter.isDiscovering) {
            // Bluetooth is already in discovery mode, we cancel to restart it again
            mBluetoothAdapter.cancelDiscovery()
        }
        mBluetoothAdapter.startDiscovery()

        Log.i(TAG, "BT discovery started")

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mReceiver, filter)
        Log.i(TAG, "Registered receiver")
    }

    fun stopDiscoverAndDisableBluetooth() {
        Log.i(TAG, "Disabling BT discovery")
        if (mBluetoothAdapter.isDiscovering) {
            mBluetoothAdapter.cancelDiscovery()
        }
        Log.i(TAG, "Disabling Bluetooth")
        mBluetoothAdapter.disable()
    }

    override fun onStopCurrentWork(): Boolean {
        Log.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy()")
        unregisterReceiver(mReceiver)
    }
}