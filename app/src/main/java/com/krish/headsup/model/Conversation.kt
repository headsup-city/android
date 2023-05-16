package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("id")
    val id: String,

    @SerializedName("lastMessage")
    val lastMessage: String?,

    @SerializedName("conversationType")
    val conversationType: String,

    @SerializedName("participants")
    val participants: List<String>,

    @SerializedName("requestedParticipants")
    val requestedParticipants: List<String>,

    @SerializedName("image")
    val image: String?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class ConversationListItem(
    @SerializedName("id")
    val id: String,

    @SerializedName("lastMessage")
    val lastMessage: LastMessage?,

    @SerializedName("conversationType")
    val conversationType: String,

    @SerializedName("participants")
    val participants: List<String>,

    @SerializedName("requestedParticipants")
    val requestedParticipants: List<String>,

    @SerializedName("image")
    val image: String?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

enum class ConversationTypes {
    GENERAL,
    JOB,
    ORDER,
    GROUP,
    EVENT
}
