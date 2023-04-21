package com.krish.headsup.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Post(
    @PrimaryKey
    val id: String,

    @SerializedName("postType")
    val postType: String?,

    @SerializedName("caption")
    val caption: String?,

    @SerializedName("attachment")
    val attachment: ApiImage?,

    // Remove the @SerializedName annotation from the imageUri field
    val imageUri: String?,

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
    val expires: String?,

    @SerializedName("createdAt")
    val createdAt: String?,

    @SerializedName("updatedAt")
    val updatedAt: String?
)

data class Location(
    @SerializedName("type")
    val type: String?,

    @SerializedName("coordinates")
    val coordinates: List<Double>?
)
