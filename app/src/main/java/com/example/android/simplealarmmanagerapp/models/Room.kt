package com.example.android.simplealarmmanagerapp.models

import com.google.gson.annotations.Expose
import java.sql.Timestamp

class Room {
    @Expose
    var id: Int? = null

    @Expose
    var number: String? = null

    @Expose
    var capacity: Int? = null

    @Expose
    var updatedAt: Timestamp? = null

    @Expose
    var createdAt: Timestamp? = null

}
