package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Section(
        @Expose
        var id: Int? = null,

        @Expose
        var courseId: Int? = null,

        @Expose
        var code: String? = null,

        @Expose
        var checksTotalDefault: Int? = null,

        @Expose
        var roomId: Int? = null,

        @Expose
        @SerializedName("passed_classes")
        val passedClasses: Int? = null,

        @Expose
        @SerializedName("attended_classes")
        val attendedClasses: Int? = null,

        @Expose
        var course: Course? = null,

        @Expose
        var room: String? = null,

        @Expose
        var sectionSlots: List<SectionSlot>? = null,

        @Expose
        var classes: List<Class>? = null
) {
    override fun toString(): String {
        return "Section { course id: ${course?.id}, " +
                "title : ${course?.title}, " +
                "roomID: $roomId}, attendedClasses: $attendedClasses," +
                "passedClasses: $passedClasses"
    }
}