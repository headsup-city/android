package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Post(
    @SerializedName("postType")
    val postType: String,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("attachment")
    val attachment: Attachment?,

    @SerializedName("author")
    val author: String,

    @SerializedName("event")
    val event: String?,

    @SerializedName("commentCount")
    val commentCount: Int,

    @SerializedName("likeCount")
    val likeCount: Int,

    @SerializedName("location")
    val location: Location,

    @SerializedName("expires")
    val expires: String?
)

data class Attachment(
    @SerializedName("name")
    val name: String?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?
)

data class Location(
    @SerializedName("type")
    val type: String,

    @SerializedName("coordinates")
    val coordinates: List<Double>
)
