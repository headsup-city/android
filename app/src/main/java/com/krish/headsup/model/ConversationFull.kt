package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class ConversationFull(
    @SerializedName("id")
    val id: String,

    @SerializedName("conversationType")
    val conversationType: String,

    @SerializedName("participants")
    val participants: List<String>,

    @SerializedName("requestedParticipants")
    val requestedParticipants: List<String>,

    @SerializedName("image")
    val image: String?,

    @SerializedName("imageUri")
    val imageUri: String?,

    @SerializedName("lastMessage")
    val lastMessage: Message?,

    @SerializedName("lastMessageAuthor")
    val lastMessageAuthor: User?,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)
