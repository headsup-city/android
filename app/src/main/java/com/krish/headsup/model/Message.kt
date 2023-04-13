package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Message(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String?,

    @SerializedName("messageType")
    val messageType: MessageType,

    @SerializedName("attachment")
    val attachment: String?,

    @SerializedName("author")
    val author: User,

    @SerializedName("conversation")
    val conversation: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

enum class MessageType {
    TEXT
}
