package com.krish.headsup.repositories

import com.krish.headsup.services.api.FollowApi
import com.krish.headsup.services.api.FollowRequest
import com.krish.headsup.services.api.UnFollowRequest
import javax.inject.Inject

class FollowRepository @Inject constructor(
    private val followApi: FollowApi
) {
    suspend fun followUser(userId: String, token: String) =
        followApi.followUser(FollowRequest(userId), token)

    suspend fun unFollowUser(userId: String, token: String) =
        followApi.unFollowUser(UnFollowRequest(userId), token)
}
