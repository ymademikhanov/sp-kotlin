package com.example.android.simplealarmmanagerapp.models

import java.sql.Timestamp


class Beacon {
    var id: String? = null
    var roomId: Int? = null
    var updatedAt: Timestamp? = null
    var createdAt: Timestamp? = null
    var room: Room? = null
}