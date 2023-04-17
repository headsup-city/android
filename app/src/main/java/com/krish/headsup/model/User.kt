package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String?,

    @SerializedName("loginId")
    val loginId: List<String>?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("mobileNumber")
    val mobileNumber: String?,

    @SerializedName("avatar")
    val avatar: String?,

    @SerializedName("avatarUri")
    val avatarUri: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("isEmailVerified")
    val isEmailVerified: Boolean?,

    @SerializedName("isMobilePhoneVerified")
    val isMobilePhoneVerified: Boolean?,

    @SerializedName("jobDoneRating")
    val jobDoneRating: JobDoneRating?,

    @SerializedName("shopInitiated")
    val shopInitiated: Boolean?,

    @SerializedName("following")
    val following: List<String>?,

    @SerializedName("block")
    val block: List<String>?,

    @SerializedName("noOfFollowing")
    val noOfFollowing: Int?,

    @SerializedName("noOfFollowedBy")
    val noOfFollowedBy: Int?,

    @SerializedName("reportedPosts")
    val reportedPosts: List<String>?,

    @SerializedName("reportedComments")
    val reportedComments: List<String>?,

    @SerializedName("likedPosts")
    val likedPosts: List<String>?,

    @SerializedName("likedComments")
    val likedComments: List<String>?,

    @SerializedName("appleUserId")
    val appleUserId: String?,

    @SerializedName("pushToken")
    val pushToken: List<String>?,

    @SerializedName("signUpSource")
    val signUpSource: SignUpSource?,

    @SerializedName("lastLogin")
    val lastLogin: String?
)

data class JobDoneRating(
    @SerializedName("total")
    val total: Int,

    @SerializedName("ratedTimes")
    val ratedTimes: Int,

    @SerializedName("points")
    val points: Int
)

data class SignUpSource(
    @SerializedName("platform")
    val platform: String,

    @SerializedName("platformType")
    val platformType: String
)
