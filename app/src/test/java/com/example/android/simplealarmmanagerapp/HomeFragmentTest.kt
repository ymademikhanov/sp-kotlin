package com.example.android.simplealarmmanagerapp

import com.example.android.simplealarmmanagerapp.fragments.HomeFragment
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeFragmentTest {
    @Test
    fun homeFragmentIsNotNull() {
        val fragment = HomeFragment()

        Assert.assertNotNull(fragment)
    }
}