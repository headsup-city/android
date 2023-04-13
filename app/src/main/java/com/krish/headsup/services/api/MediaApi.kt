package com.krish.headsup.services.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface MediaApi {
    @GET("media/getUrl/{key}")
    suspend fun getImageWithKey(
        @Path("key") key: String,
        @Header("Authorization") token: String
    ): Response<String>
}