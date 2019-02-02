package com.example.android.simplealarmmanagerapp.models

import java.sql.Timestamp

class Section(var id: Int? = null,
              var courseId: Int? = null,
              var code: String? = null,
              var checksTotalDefault: Int? = null,
              var roomId: Int? = null,

              var course: Course? = null,
              var room: String? = null,
              var sectionSlots: List<SectionSlot>? = null,
              var classes: List<Class>? = null
) {
    override fun toString(): String {
        return "Section{ course id: $courseId, code : $code, roomID: $roomId} "
    }
}