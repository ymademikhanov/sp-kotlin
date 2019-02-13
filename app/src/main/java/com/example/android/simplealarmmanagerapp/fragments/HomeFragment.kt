package com.example.android.simplealarmmanagerapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.widget.PullRefreshLayout
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.adapters.CourseInfoAdapter
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPI
import com.example.android.simplealarmmanagerapp.utilities.network.StudentAPIClient
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_DARK_COLOR
import com.forms.sti.progresslitieigb.ProgressLoadingIGB
import com.forms.sti.progresslitieigb.finishLoadingIGB
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    val TAG = "HomeFragment"
    lateinit var preferences: SharedPreferences
    lateinit var courseInfoAdapter: CourseInfoAdapter
    lateinit var courseInfoRV: RecyclerView
    var sections = ArrayList<Section>()
    lateinit var refreshLayout: PullRefreshLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferences = context!!.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)
        initUI(view)

        fetchData()
    }

    private fun initUI(view: View) {
        courseInfoRV = view.findViewById(R.id.courseInfoRV) as RecyclerView
        courseInfoRV.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        courseInfoRV.layoutManager = LinearLayoutManager(context)
        courseInfoAdapter = CourseInfoAdapter(sections)
        courseInfoRV.adapter = courseInfoAdapter


        refreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        refreshLayout.setColor(PRIMARY_DARK_COLOR)

        refreshLayout.setOnRefreshListener {
            fetchData()
            refreshLayout.setRefreshing(false)
        }
    }

    private fun fetchData() {
        showAnimation("Loading course inf,o", R.raw.progress_animation, 5000)

        val jwt = preferences.getString("jwt", "")
        val jwtMap = mapOf("x-auth" to jwt)

        val client = StudentAPIClient.client.create<StudentAPI>(StudentAPI::class.java)
        client.listSectionsWithAttendance(jwtMap).enqueue(object : Callback<List<Section>> {
            override fun onResponse(call: Call<List<Section>>, response: Response<List<Section>>) {
                if (response.isSuccessful) {
                    // tasks available
                    val tempSections = response.body()

                    for (section in tempSections!!) {
                        Log.i(TAG, "$section")
                    }

                    sections.clear()
                    sections.addAll(tempSections)
                    courseInfoAdapter.notifyDataSetChanged()
                    finishAnimation()

                    Log.i(TAG, "adapter $sections")
                } else {
                    showAnimation(response.errorBody().toString(), R.raw.error_animation, 2000)
                }
            }

            override fun onFailure(call: Call<List<Section>>, t: Throwable) {
                // something went completely south (like no internet connection)
                showAnimation("No network!", R.raw.error_animation, 2000)
                Log.d("Error", t.message)
            }
        })
    }

    private fun showAnimation(msg: String, animation: Int, duration: Int) {
        finishAnimation()
        ProgressLoadingIGB.startLoadingIGB(context!!) {
            message = msg
            srcLottieJson = animation
            timer = duration
        }
    }

    private fun finishAnimation() {
        context!!.finishLoadingIGB()
    }
}
