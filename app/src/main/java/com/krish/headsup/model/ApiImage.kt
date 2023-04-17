package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class ApiImage(
    @SerializedName("name")
    val name: String?,

    @SerializedName("width")
    val width: Int?,

    @SerializedName("height")
    val height: Int?,

    @SerializedName("uri")
    val uri: String?
)
