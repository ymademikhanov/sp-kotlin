package com.example.android.simplealarmmanagerapp.models

import ir.mirrajabi.searchdialog.core.Searchable

class SearchTargetDeviceModel(private var mTitle:String?) : Searchable {
    override fun getTitle(): String {
        return mTitle!!
    }
}