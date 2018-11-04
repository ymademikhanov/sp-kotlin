package com.example.android.simplealarmmanagerapp.fragments

import android.app.Fragment
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import org.json.JSONArray

class SectionListFragment : Fragment() {
    val TAG = "SectionListFragment"
    val SECTION_URL = "https://attendance-app-dev.herokuapp.com/api/v1/me/sections"

    private lateinit var preferences: SharedPreferences
    lateinit var sectionListView: View
    lateinit var courseListView : ListView

    var sectionList: ArrayList<String> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sectionListView = inflater.inflate(R.layout.section_list_layout, container, false)
        return sectionListView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        courseListView = view.findViewById(R.id.section_list_view)

        preferences = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        val jwt = preferences.getString("jwt", "")

        Log.i(TAG, "Started loading sections by jwt $jwt")
        SectionListLoaderInBackground().execute(jwt)
    }

    inner class SectionListLoaderInBackground: AsyncTask<String, String, JSONArray>() {
        override fun doInBackground(vararg jwts: String): JSONArray {
            val jwt = jwts[0]
            val response = khttp.get(SECTION_URL, headers=mapOf("x-auth" to jwt))
            Log.i(TAG, "Response: ${response.jsonArray}")
            return response.jsonArray
        }

        override fun onPostExecute(sections: JSONArray) {
            for (i in 0..(sections.length() - 1)) {
                val section = sections.getJSONObject(i).getJSONObject("course").getString("title")
                sectionList.add(section)
                Log.i(TAG, "Section: $section")
            }

            var adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, sectionList)
            courseListView.adapter = adapter
        }
    }

}