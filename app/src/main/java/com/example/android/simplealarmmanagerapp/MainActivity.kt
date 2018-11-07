package com.example.android.simplealarmmanagerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.android.simplealarmmanagerapp.models.SearchTargetDeviceModel
import com.example.android.simplealarmmanagerapp.models.TargetDeviceModel
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat
import ir.mirrajabi.searchdialog.core.SearchResultListener
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Math.max
import java.util.*
import kotlin.collections.ArrayList
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.RealmResults

class MainActivity : AppCompatActivity() {

    var TAG = "MainActivity"

    val realm by lazy{Realm.getDefaultInstance()}
    lateinit var context : Context
    lateinit var alarmManager : AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)
        var config = RealmConfiguration.Builder().name("target_devices.realm").build()
        Realm.setDefaultConfiguration(config)

        target_beacon_address_btn.setOnClickListener {
            SimpleSearchDialogCompat(this@MainActivity,
                "Search", "Type for target device address",
                null,
                initData(),
                SearchResultListener{ baseSearchDialogCompat, item, position ->
                    et_target_device_address.setText(item.title)
                    baseSearchDialogCompat.dismiss()
            }).show()
        }

        save_target_device_btn.setOnClickListener {
            realm.executeTransaction {
                val device = realm.createObject(TargetDeviceModel::class.java)
                device.Address = et_target_device_address.text.toString()
            }
        }

        ShowLog(intent.getStringExtra("JWToken"))

        context = this
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        btn_create.setOnClickListener {
            val start_within = et_start_within.text.toString().toInt() * 1000
            val interval = max(et_interval.text.toString().toInt(), 15) * 1000
            var checks_number = max(et_count.text.toString().toInt(), 1)

            val offset = System.currentTimeMillis()

            ShowLog("Offset is " + offset)

            var start_time = start_within
            while (checks_number > 0) {


                val intent = Intent(context, BeaconScanner::class.java)
                val pendingIntent = PendingIntent.getBroadcast(context, Random().nextInt(1000000), intent, PendingIntent.FLAG_ONE_SHOT)
                alarmManager.setExact(AlarmManager.RTC,offset + start_time, pendingIntent)

                ShowLog("Alarm was created for " + start_time + " millis.")

                start_time += interval
                checks_number -= 1
            }
        }
    }

    fun initData() : ArrayList<SearchTargetDeviceModel> {
        val items = ArrayList<SearchTargetDeviceModel>()

        val targetDevices: RealmResults<TargetDeviceModel> = realm.where(TargetDeviceModel::class.java).findAll()
        targetDevices?.forEach {
            items.add(SearchTargetDeviceModel(it.Address))
        }

        return items
    }

    fun ShowLog(message: String) {
        Log.w(TAG, Date().toString() + message)
    }
}
