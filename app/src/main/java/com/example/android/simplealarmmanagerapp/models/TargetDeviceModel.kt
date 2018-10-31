package com.example.android.simplealarmmanagerapp.models

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TargetDeviceModel(): RealmObject() {
    @PrimaryKey
    var Id: Long = 0
    var Address: String? = null
}