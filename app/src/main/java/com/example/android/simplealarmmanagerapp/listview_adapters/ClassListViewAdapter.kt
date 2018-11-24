package com.example.android.simplealarmmanagerapp.listview_adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.android.simplealarmmanagerapp.R
import com.example.android.simplealarmmanagerapp.listview_models.ClassListViewModel

class ClassListViewAdapter(var mCtx: Context, var resource: Int, var items: List<ClassListViewModel>)
    : ArrayAdapter<ClassListViewModel>(mCtx, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater:LayoutInflater = LayoutInflater.from(mCtx)
        val view:View = layoutInflater.inflate(resource, null)

        val titleTextView:TextView = view.findViewById(R.id.class_list_view_row_title)
        val attendanceInfoTextView:TextView = view.findViewById(R.id.class_list_view_row_attendance_info)

        titleTextView.text = items[position].title
        attendanceInfoTextView.text = items[position].attendanceInfo

        return view
    }
}