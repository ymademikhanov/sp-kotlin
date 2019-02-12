package com.example.android.simplealarmmanagerapp.utilities

import java.sql.Timestamp
import java.util.*

fun getDateTime(t: Long): String {
    val stamp = Timestamp(t)
    val date = Date(stamp.time)
    return date.toString()
}