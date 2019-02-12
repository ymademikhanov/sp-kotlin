package com.example.android.simplealarmmanagerapp.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.activities.SignInActivity
import com.example.android.simplealarmmanagerapp.receivers.WeeklyCheckLoader
import com.example.android.simplealarmmanagerapp.utilities.LocalAccountManager
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.network.NetworkManager
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

                    val account = LocalAccountManager.loadAccount(preferences)
                    WeeklyCheckLoader.schedule(context!!, account!!)
                }
            }
        })

        signOutButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                LocalAccountManager.deleteAccount(preferences)
                val signInActivityIntent = Intent(context, SignInActivity::class.java)
                startActivity(signInActivityIntent)
                activity?.finish()
            }
        })
    }
}
