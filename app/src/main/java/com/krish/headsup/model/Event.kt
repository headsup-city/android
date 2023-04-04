package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Event(
    val id: String,
    val description: String?,
    val images: List<ApiImage>?,
    @SerializedName("when")
    val whenTime: String?,
    val title: String?,
    val creator: User?,
    val participants: List<User>?,
    val post: String?,
    @SerializedName("conversation")
    val conversationId: String?,
    val location: Location?,
    val createdAt: String?,
    val updatedAt: String?
)
