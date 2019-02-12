package com.example.android.simplealarmmanagerapp.fragments

import androidx.lifecycle.LiveData
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.utilities.network.Resource

interface SectionRepository {
    fun refresh(jwt: Map<String, String>)
    fun getCourses(jwt: Map<String, String>) : LiveData<Resource<List<Section>>>
}