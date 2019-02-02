package com.example.android.simplealarmmanagerapp.utilities.network

class Resource<T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        fun <T> success(data: T): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }
        fun <T> error(data: T?, message: String): Resource<T> {
            return Resource(Status.ERROR, data, message)
        }
    }

    enum class Status { SUCCESS, ERROR }
}