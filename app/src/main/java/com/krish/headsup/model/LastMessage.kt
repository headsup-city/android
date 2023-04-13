package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class LastMessage(
    @SerializedName("message")
    val message: Message,

    @SerializedName("byUser")
    val byUser: User
)
