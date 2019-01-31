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
//    var students: List<Student>? = null,
//    var instructors: List<Instructor>? = null,
    var classes: List<Class>? = null
)