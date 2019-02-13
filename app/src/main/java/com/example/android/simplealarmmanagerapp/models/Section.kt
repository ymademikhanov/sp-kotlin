package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.SerializedName

class Section(var id: Int? = null,

              var courseId: Int? = null,

              var code: String? = null,

              var checksTotalDefault: Int? = null,

              var roomId: Int? = null,

              @SerializedName("passed_classes")
              val passedClasses: Int? = null,

              @SerializedName("attended_classes")
              val attendedClasses: Int? = null,

              var course: Course? = null,

              var room: String? = null,

              var sectionSlots: List<SectionSlot>? = null,

              var classes: List<Class>? = null
) {
    override fun toString(): String {
        return "Section { course id: ${course?.id}, " +
                "title : ${course?.title}, " +
                "roomID: $roomId}, attendedClasses: $attendedClasses," +
                "passedClasses: $passedClasses"
    }
}