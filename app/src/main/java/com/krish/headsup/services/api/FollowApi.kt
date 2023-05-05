package com.krish.headsup.services.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class FollowRequest(
    val toFollow: String
)

data class UnFollowRequest(
    val toUnFollow: String
)

interface FollowApi {
    @POST("follow/followUser")
    suspend fun followUser(
        @Body followRequest: FollowRequest,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("follow/unfollowUser")
    suspend fun unFollowUser(
        @Body unfollowRequest: UnFollowRequest,
        @Header("Authorization") token: String
    ): Response<Unit>
}
