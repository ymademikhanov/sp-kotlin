package com.example.android.simplealarmmanagerapp.fragments
import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.utilities.constants.TARGET_BEACON_ADDRESS_CONST
import com.example.android.simplealarmmanagerapp.utilities.constants.TARGET_DEVICE_PREFERENCES_NAME

class SetTargetBeaconFragment : Fragment() {
    lateinit var preferences: SharedPreferences
    lateinit var setTargetBeaconView: View
    lateinit var saveButton: Button
    lateinit var targetDeviceAddrET: EditText

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?)
            : View {
        setTargetBeaconView =
                inflater.inflate(R.layout.set_target_beacon_layout, container, false)
        return setTargetBeaconView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences =
                activity.getSharedPreferences(TARGET_DEVICE_PREFERENCES_NAME, Context.MODE_PRIVATE)

        val targetBeaconAddress =
                preferences.getString(TARGET_BEACON_ADDRESS_CONST, "")

        targetDeviceAddrET = view.findViewById(R.id.target_beacon_address_et)
        targetDeviceAddrET.setText(targetBeaconAddress)

        saveButton = view.findViewById(R.id.set_target_beacon_btn)
        saveButton.setOnClickListener {
            val editor = preferences.edit()
            editor.putString(TARGET_BEACON_ADDRESS_CONST, targetDeviceAddrET.text.toString())
            editor.apply()
        }
    }
}