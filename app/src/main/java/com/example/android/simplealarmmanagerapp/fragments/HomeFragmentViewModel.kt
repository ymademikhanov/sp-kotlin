package com.example.android.simplealarmmanagerapp.fragments

import androidx.lifecycle.ViewModel

class HomeFragmentViewModel(private val sectionRepository: SectionRepository) : ViewModel() {
    fun refresh(jwt: Map<String, String>) = sectionRepository.refresh(jwt)
    fun getCourses(jwt: Map<String, String>) = sectionRepository.getCourses(jwt)
}