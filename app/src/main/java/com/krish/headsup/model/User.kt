package com.krish.headsup.model

data class User(
    val id: String,
    val name: String?,
    val loginId: List<String>?,
    val email: String?,
    val mobileNumber: String?,
    val avatar: String?,
    val role: String?,
    val isEmailVerified: Boolean?,
    val isMobilePhoneVerified: Boolean?,
    val jobDoneRating: JobDoneRating?,
    val shopInitiated: Boolean?,
    val following: List<String>?,
    val block: List<String>?,
    val noOfFollowing: Int?,
    val noOfFollowedBy: Int?,
    val reportedPosts: List<String>?,
    val reportedComments: List<String>?,
    val likedPosts: List<String>?,
    val likedComments: List<String>?,
    val appleUserId: String?,
    val pushToken: List<String>?,
    val signUpSource: SignUpSource?,
    val lastLogin: String?
)

data class JobDoneRating(
    val total: Int,
    val ratedTimes: Int,
    val points: Int
)

data class SignUpSource(
    val platform: String,
    val platformType: String
)
