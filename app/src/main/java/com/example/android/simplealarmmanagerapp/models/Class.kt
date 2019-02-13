package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Class(
        @Expose
        val id: Int? = null,

        @Expose
        @SerializedName("section_id") val sectionId: Int? = null,

        @Expose
        val start: Long = 0,

        @Expose
        val end: Long = 0,

        @Expose
        @SerializedName("room_id")
        val roomId: Int? = null,

        @Expose
        @SerializedName("checks")
        val checks: Int? = null,

        @Expose
        val course: Course? = null,

        @Expose
        val room: String? = null,

        @Expose
        val beacon: Beacon? = null,

        @Expose
        val section: Section? = null) {

    override fun toString(): String {
        return "Section{ id: $id, section id: $sectionId, room: $room} "
    }
}
