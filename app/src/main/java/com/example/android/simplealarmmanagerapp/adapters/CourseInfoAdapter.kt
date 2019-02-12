package com.example.android.simplealarmmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.models.Section
import kotlinx.android.synthetic.main.course_rate_list_item.*
import kotlinx.android.synthetic.main.course_rate_list_item.view.*

class CourseInfoAdapter(private val courses: ArrayList<Section>): RecyclerView.Adapter<CourseInfoAdapter.CourseInfoViewHolder>() {
    class CourseInfoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val courseTitleTV = view.courseTitleTV
        val absentTV = view.absentTV
        val totalTV = view.totalTV
        val rateTV = view.rateTV
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseInfoViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.course_rate_list_item, parent, false) as LinearLayout

        return CourseInfoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CourseInfoViewHolder, position: Int) {
        holder.courseTitleTV.text = courses[position].course?.title
        val attended = courses[position].attendedClasses
        val passed = courses[position].passedClasses
        holder.absentTV.text = attended.toString()
        holder.totalTV.text = passed.toString()
        holder.rateTV.text = (attended!! * 100 / passed!!).toString()
    }

    override fun getItemCount() = courses.size
}