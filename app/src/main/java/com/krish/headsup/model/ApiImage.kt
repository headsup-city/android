package com.krish.headsup.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ApiImage(
    @SerializedName("name")
    val name: String?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("uri")
    val uri: String?
) : Parcelable
