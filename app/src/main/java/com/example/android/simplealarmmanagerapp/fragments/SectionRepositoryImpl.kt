package com.example.android.simplealarmmanagerapp.fragments

import androidx.lifecycle.LiveData
import com.example.android.simplealarmmanagerapp.models.Section

class SectionRepositoryImpl(private val sectionDao: SectionDao): SectionRepository {
    override fun refresh(jwt: Map<String, String>) = sectionDao.refresh(jwt)
    override fun getCourses(jwt: Map<String, String>) = sectionDao.getCourses(jwt)
}