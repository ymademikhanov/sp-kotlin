package com.example.android.simplealarmmanagerapp.models

import java.sql.Timestamp


class Course {
    var id: Int = 0
    lateinit var abbreviation: String
    lateinit var title: String
    var updatedAt: Timestamp? = null
    var createdAt: Timestamp? = null

    //    Dependent fields
    var sections: List<Section>? = null

    constructor() {}

    constructor(id: Int, abbreviation: String, title: String) {
        this.id = id
        this.abbreviation = abbreviation
        this.title = title
    }
}
