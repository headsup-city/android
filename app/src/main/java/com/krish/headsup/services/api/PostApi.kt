package com.krish.headsup.services.api

import com.krish.headsup.model.Post
import com.krish.headsup.model.PostListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @Multipart
    @POST("post/createPrimaryPost")
    suspend fun uploadPrimaryPost(
        @Header("Authorization") token: String,
        @Part body: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("post/createShortPost")
    suspend fun uploadShortPost(
        @Header("Authorization") token: String,
        @Part body: MultipartBody.Part
    ): Response<Unit>

    @Multipart
    @POST("post/createEventPost")
    suspend fun uploadEventPost(
        @Header("Authorization") token: String,
        @Part body: MultipartBody.Part
    ): Response<Unit>

    @GET("post/general")
    suspend fun getGeneralPost(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<PostListResponse>

    @GET("post/{id}")
    suspend fun getPost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Post>

    @GET("post/user/{id}/getPosts")
    suspend fun getPostsForUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int
    ): Response<PostListResponse>

    @POST("post/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PostListResponse>

    @POST("post/{id}/unlike")
    suspend fun unlikePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<PostListResponse>

    @DELETE("post/{id}")
    suspend fun deleteAPost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Post>
}
