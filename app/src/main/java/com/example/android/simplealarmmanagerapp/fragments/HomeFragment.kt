package com.example.android.simplealarmmanagerapp.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baoyz.widget.PullRefreshLayout
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.adapters.CourseInfoAdapter
import com.example.android.simplealarmmanagerapp.utilities.constants.AUTH_PREFERENCE_NAME
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.utilities.constants.PRIMARY_DARK_COLOR
import com.example.android.simplealarmmanagerapp.utilities.network.Resource
import com.forms.sti.progresslitieigb.ProgressLoadingIGB
import com.forms.sti.progresslitieigb.finishLoadingIGB
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class HomeFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val viewModelFactory: HomeFragmentViewModelFactory by instance()

    val TAG = "HomeFragment"
    lateinit var preferences: SharedPreferences
    lateinit var courseInfoAdapter: CourseInfoAdapter
    lateinit var courseInfoRV: RecyclerView
    lateinit var viewModel: HomeFragmentViewModel
    lateinit var jwt: String

    var sections = ArrayList<Section>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = context!!.getSharedPreferences(AUTH_PREFERENCE_NAME, Context.MODE_PRIVATE)

        // Initializing UI.
        initUI(view)

        // By default we fetch the data.
        fetchData()
    }

    private fun initUI(view: View) {
        // Configuring RecyclerView for courses.
        courseInfoRV = view.findViewById(R.id.courseInfoRV) as RecyclerView
        courseInfoRV.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        courseInfoRV.layoutManager = LinearLayoutManager(context)

        // Setting RecyclerView Adapter.
        courseInfoAdapter = CourseInfoAdapter(sections)
        courseInfoRV.adapter = courseInfoAdapter

        // Setting refresh layout
        val refreshLayout = view.findViewById(R.id.swipeRefreshLayout) as PullRefreshLayout
        refreshLayout.setColor(PRIMARY_DARK_COLOR)

        // Providing logic when swipe down refresh layout.
        refreshLayout.setOnRefreshListener {
            fetchData()
            refreshLayout.setRefreshing(false)
        }

        // Extracting JWT.
        jwt = preferences.getString("jwt", "")!!
        val jwtMap = mapOf("x-auth" to jwt)

        // Setting ViewModel.
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(HomeFragmentViewModel::class.java)

        // Subscribing to courses loading from repository.
        viewModel.getCourses(jwtMap).observe(this, Observer { sectionsResource ->
            // Handling subscription updates.
            when(sectionsResource.status) {
                Resource.Status.SUCCESS -> {
                    sections = sectionsResource.data!! as ArrayList<Section>
                    courseInfoAdapter.notifyDataSetChanged()
                    finishAnimation()
                }
                Resource.Status.ERROR -> {
                    showErrorAnimation(sectionsResource.message!!)
                }
            }
        })
    }

    private fun finishAnimation() {
        context!!.finishLoadingIGB()
    }

    private fun showLoadingAnimation() {
        finishAnimation()
        ProgressLoadingIGB.startLoadingIGB(context!!) {
            message = "Loading course info!"
            srcLottieJson = R.raw.progress_animation
            timer = 5000
        }
    }

    private fun showErrorAnimation(errorMessage: String) {
        finishAnimation()
        ProgressLoadingIGB.startLoadingIGB(context!!) {
            message = errorMessage
            srcLottieJson = R.raw.loading_error
            timer = 2000
        }
    }

    private fun fetchData() {
        viewModel.refresh(mapOf("x-auth" to jwt))
        showLoadingAnimation()
    }
}
