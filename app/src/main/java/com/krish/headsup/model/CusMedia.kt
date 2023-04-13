package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class CusMedia(
    @SerializedName("name")
    val name: String,

    @SerializedName("uri")
    val uri: String?,

    @SerializedName("height")
    val height: Int,

    @SerializedName("width")
    val width: Int
)
