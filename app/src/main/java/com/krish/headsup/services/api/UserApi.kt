package com.krish.headsup.services.api

import com.krish.headsup.model.User
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): User
}
