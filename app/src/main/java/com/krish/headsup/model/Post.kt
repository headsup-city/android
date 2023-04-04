package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Post(
    val id: String,

    @SerializedName("postType")
    val postType: String?,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("attachment")
    val attachment: ApiImage?,

    @SerializedName("author")
    val author: User?,

    @SerializedName("event")
    val event: Event?,

    @SerializedName("commentCount")
    val commentCount: Int?,

    @SerializedName("likeCount")
    val likeCount: Int?,

    @SerializedName("location")
    val location: Location?,

    @SerializedName("expires")
    val expires: String?
)

data class Location(
    @SerializedName("type")
    val type: String?,

    @SerializedName("coordinates")
    val coordinates: List<Double>?
)
