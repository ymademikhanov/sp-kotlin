package com.example.android.simplealarmmanagerapp

import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.example.android.simplealarmmanagerapp.constants.PREFERENCES_NAME
import com.example.android.simplealarmmanagerapp.constants.SECTION_ID_EXTRA
import com.example.android.simplealarmmanagerapp.constants.SECTIONS_URL
import org.json.JSONArray
import com.example.android.simplealarmmanagerapp.models.Class
import com.google.gson.Gson
import java.util.*

class ClassListActivity : AppCompatActivity() {
    val TAG = "ClassListActivity"

    private lateinit var preferences: SharedPreferences

    lateinit var classListView : ListView
    var classList: ArrayList<Class> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class_list)

        Log.i(TAG, "onCreate()")

        preferences = getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        classListView = findViewById(R.id.class_list_view)

        val sectionId = intent.getIntExtra(SECTION_ID_EXTRA, 0)

        Log.i(TAG, "Section ID is $sectionId")

        ClassListLoaderInBackground().execute(sectionId)
    }

    inner class ClassListLoaderInBackground: AsyncTask<Int?, String, JSONArray>() {
        override fun doInBackground(vararg sectionIds: Int?): JSONArray {
            val jwt = preferences.getString("jwt", "")
            val sectionId = sectionIds[0]
            val url = "$SECTIONS_URL/$sectionId/classes"

            Log.i(TAG, "Url: $url")

            val response = khttp.get(url, headers=mapOf("x-auth" to jwt))

            Log.i(TAG, "Response: ${response}")

            Log.i(TAG, "Response: ${response.jsonArray}")

            return response.jsonArray
        }

        override fun onPostExecute(classes: JSONArray) {
            for (i in 0..(classes.length() - 1)) {
                val obj = classes.getJSONObject(i)
                val objStr = obj.toString()
                val universityClass = Gson().fromJson(objStr, Class::class.java)
                Log.i(TAG, "Class2: $universityClass")
                classList.add(universityClass)
            }

            var adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, classList)
            classListView.adapter = adapter
        }
    }
}