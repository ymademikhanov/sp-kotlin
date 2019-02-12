package com.example.android.simplealarmmanagerapp.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.simplealarmmanagerapp.models.Section
import com.example.android.simplealarmmanagerapp.utilities.network.Resource

class SectionDaoRoom: SectionDao {
    private val sectionList = mutableListOf<Section>()
    private val sectionsResource = MutableLiveData<Resource<List<Section>>>()

    init {
        sectionList.add(Section(code="CSCI151"))
        sectionList.add(Section(code="CalcIII"))
        sectionList.add(Section(code="MATH274"))
        sectionsResource.value = Resource.success(sectionList)
    }

    override fun refresh(jwt: Map<String, String>) {}

    override fun getCourses(jwt: Map<String, String>) = sectionsResource as LiveData<Resource<List<Section>>>
}