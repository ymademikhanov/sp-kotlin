package com.example.android.simplealarmmanagerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.simplealarmmanagerapp.activities.MainActivity
import com.example.android.simplealarmmanagerapp.activities.SignInActivity
import com.example.android.simplealarmmanagerapp.models.Account
import com.example.android.simplealarmmanagerapp.receivers.WeeklyBTCheckScheduler
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.constants.WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX
import com.example.android.simplealarmmanagerapp.utilities.network.NetworkManager
import khronos.*
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {
    val TAG = "SettingsFragment"
    lateinit var preferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = context!!.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        scheduleChecksButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (NetworkManager.isNetworkAvailable(context!!)) {
                    Log.i(TAG, "Scheduling weekly attedance schedule loading.")
                    scheduleWeeklyLoadingOfAttendanceChecks()
                }
            }
        })

        signOutButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                deleteAccountFromDevice()
                val signInActivityIntent = Intent(context, SignInActivity::class.java)
                startActivity(signInActivityIntent)
                activity?.finish()
            }
        })
    }

    fun scheduleWeeklyLoadingOfAttendanceChecks() {
        val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        Log.i(TAG, "Setting weekly attendance checks scheduling...")

        val account = loadSavedAccount()
        if (account?.type == "student") {
            // SETTING REPEATED ATTENDANCE CHECK LOADING ON EVERY SUNDAY.
            val today = Dates.today
            var thisSunday = today.with(weekday = 1)
            thisSunday = thisSunday.beginningOfHour

            for (i in 0..15) {
                if (thisSunday.time > System.currentTimeMillis() - 24 * 60 * 60 * 1000) {
                    val intent = Intent(context, WeeklyBTCheckScheduler::class.java)
                    val alarmId = "$WEEKLY_ATTENDANCE_CHECK_LOADER_PREFIX:$account.id:$i"
                    val alarmIdHashCode = alarmId.hashCode()
                    val pendingIntent = PendingIntent.getBroadcast(context, alarmIdHashCode, intent, PendingIntent.FLAG_CANCEL_CURRENT)
                    alarmManager.setExact(AlarmManager.RTC, thisSunday.time, pendingIntent)
                    Log.i(TAG, "Scheduled an attendance checks loading on $thisSunday")
                }
                thisSunday += 1.week
            }
        }
    }

    private fun loadSavedAccount() : Account? {
        if (preferences.getBoolean("signedIn", false)) {
            val email = preferences.getString("email", "")
            val password = preferences.getString("password", "")
            val type = preferences.getString("type", "")
            val id = preferences.getInt("id", 0)
            var account = Account(email!!, password!!, null, null)
            account.type = type
            account.id = id
            return account
        }
        return null
    }

    private fun deleteAccountFromDevice() {
        val editor = preferences.edit()
        editor.remove("email")
        editor.remove("password")
        editor.remove("id")
        editor.remove("type")
        editor.remove("signedIn")
        editor.remove("jwt")
        editor.apply()
    }
}
