package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class Comment(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("post")
    val post: String,

    @SerializedName("author")
    val author: User,

    @SerializedName("parentComment")
    val parentComment: String?,

    @SerializedName("commentCount")
    val commentCount: Int,

    @SerializedName("likeCount")
    val likeCount: Int,

    @SerializedName("childComments")
    val childComments: List<BasicComment>?,

    @SerializedName("likedByUser")
    val likedByUser: Boolean,

    @SerializedName("disabled")
    val disabled: Boolean,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("createdAt")
    val createdAt: String
)

data class BasicComment(
    @SerializedName("id")
    val id: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("post")
    val post: String,

    @SerializedName("author")
    val author: User,

    @SerializedName("parentComment")
    val parentComment: String?,

    @SerializedName("commentCount")
    val commentCount: Int,

    @SerializedName("likeCount")
    val likeCount: Int,

    @SerializedName("likedByUser")
    val likedByUser: Boolean,

    @SerializedName("disabled")
    val disabled: Boolean,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("createdAt")
    val createdAt: String
)
