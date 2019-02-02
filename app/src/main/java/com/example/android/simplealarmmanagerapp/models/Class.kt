package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.SerializedName

data class Class(val id: Int? = null,
                 @SerializedName("section_id") val sectionId: Int? = null,
                 val start: Long = 0,
                 val end: Long = 0,
                 @SerializedName("room_id") val roomId: Int? = null,
                 @SerializedName("checks") val checks: Int? = null,
                 val course: Course? = null,
                 val room: String? = null,
                 val beacon: Beacon? = null,
                 val section: Section? = null) {

    override fun toString(): String {
        return "Section{ id: $id, section id: $sectionId, room: $room} "
    }
}
