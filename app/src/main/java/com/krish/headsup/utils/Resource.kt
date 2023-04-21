package com.krish.headsup.utils

sealed class Resource<T>(val status: Status, val data: T? = null, val message: String? = null) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    class Success<T>(data: T) : Resource<T>(Status.SUCCESS, data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(Status.ERROR, data, message)
    class Loading<T>(data: T? = null) : Resource<T>(Status.LOADING, data)

    fun <R> appendData(newData: R?): Resource<T> {
        return if (this.status == Status.SUCCESS && this.data is List<*> && newData is List<*>) {
            @Suppress("UNCHECKED_CAST")
            Success((this.data as List<T>) + (newData as List<T>)) as Resource<T>
        } else {
            this
        }
    }

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(message: String, data: T? = null): Resource<T> = Error(message, data)
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)
    }
}
