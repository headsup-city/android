package com.krish.headsup.services.api

import com.krish.headsup.model.Post
import retrofit2.http.GET

interface PostApi {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
