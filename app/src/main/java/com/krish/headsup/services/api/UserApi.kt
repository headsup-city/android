package com.krish.headsup.services.api

import com.krish.headsup.model.User
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApi {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): User

    @POST("users/{id}/send-token")
    suspend fun sendToken(@Path("id") userId: String, @Body token: String): ResponseBody
}
