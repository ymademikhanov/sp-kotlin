package com.example.android.simplealarmmanagerapp.fragments

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.ClassListActivity
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.constants.SECTION_ID_EXTRA
import com.example.android.simplealarmmanagerapp.constants.MY_SECTION_URL
import com.example.android.simplealarmmanagerapp.models.Section
import com.google.gson.Gson
import org.json.JSONArray

class SectionListFragment : Fragment() {
    val TAG = "SectionListFragment"

    private lateinit var preferences: SharedPreferences
    lateinit var fragmentView: View
    lateinit var sectionListView : ListView

    var sectionList: ArrayList<Section> = ArrayList()
    lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentView = inflater.inflate(R.layout.section_list_layout, container, false)
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(activity)

        sectionListView = view.findViewById(R.id.section_list_view)
        sectionListView.setOnItemClickListener { parent, view, position, id ->
            val editor = preferences.edit()
            editor.putInt(SECTION_ID_EXTRA, sectionList[position].id!!)
            editor.commit()

            val fr = ClassListFragment()
            val fragmentManager = getFragmentManager()
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.content_frame, fr)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val jwt = preferences.getString("jwt", "")

        Log.i(TAG, "Started loading sections by jwt $jwt")

        progressDialog.setMessage("Loading sections ...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        SectionListLoaderInBackground().execute(jwt)
    }

    inner class SectionListLoaderInBackground: AsyncTask<String, String, JSONArray>() {
        override fun doInBackground(vararg jwts: String): JSONArray {
            sectionList.clear()

            val jwt = jwts[0]
            val response = khttp.get(MY_SECTION_URL, headers=mapOf("x-auth" to jwt))

            Log.i(TAG, "Response: ${response.jsonArray}")

            return response.jsonArray
        }

        override fun onPostExecute(sections: JSONArray) {
            for (i in 0..(sections.length() - 1)) {
                val obj = sections.getJSONObject(i)
                val objStr = obj.toString()
                Log.i(TAG, "Obj JSON is $objStr")
                val section = Gson().fromJson(objStr, Section::class.java)
                Log.i(TAG, "Section: $section")
                sectionList.add(section)
            }

            var adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, sectionList)
            sectionListView.adapter = adapter

            progressDialog.hide()
        }
    }
}