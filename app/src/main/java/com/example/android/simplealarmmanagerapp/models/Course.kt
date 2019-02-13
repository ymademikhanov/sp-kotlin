package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import java.sql.Timestamp


class Course {
    @Expose
    var id: Int = 0

    @Expose
    lateinit var abbreviation: String

    @Expose
    lateinit var title: String

    @Expose
    var updatedAt: Timestamp? = null

    @Expose
    var createdAt: Timestamp? = null

    //    Dependent fields
    @Expose
    var sections: List<Section>? = null

    constructor() {}

    constructor(id: Int, abbreviation: String, title: String) {
        this.id = id
        this.abbreviation = abbreviation
        this.title = title
    }
}
