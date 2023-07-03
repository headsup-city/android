package com.krish.headsup.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
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

    @SerializedName("author")
    val author: User?,

    @SerializedName("event")
    val event: @RawValue Event?,

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
    val updatedAt: String?,

    // Local field
    var isReportedLocal: Boolean? = false
) : Parcelable

@Parcelize
data class Location(
    @SerializedName("type")
    val type: String?,

    @SerializedName("coordinates")
    val coordinates: List<Double>?
) : Parcelable
