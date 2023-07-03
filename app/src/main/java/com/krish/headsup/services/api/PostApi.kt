package com.krish.headsup.services.api

import com.krish.headsup.model.Post
import com.krish.headsup.model.PostListResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PostApi {
    @POST("/v1/post/createPrimaryPost")
    suspend fun uploadPrimaryPost(
        @Header("Authorization") token: String,
        @Body body: MultipartBody
    ): Response<Unit>

    @Multipart
    @POST("/v1/post/createShortPost")
    suspend fun uploadShortPost(
        @Header("Authorization") token: String,
        @Part body: MultipartBody
    ): Response<Unit>

    @Multipart
    @POST("/v1/post/createEventPost")
    suspend fun uploadEventPost(
        @Header("Authorization") token: String,
        @Part body: MultipartBody
    ): Response<Unit>

    @GET("/v1/post/general")
    suspend fun getGeneralPost(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<PostListResponse>

    @GET("/v1/post/{id}")
    suspend fun getPost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Post>

    @GET("/v1/post/user/{id}/getPosts")
    suspend fun getPostsForUser(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("page") page: Int
    ): Response<PostListResponse>

    @POST("/v1/post/{id}/like")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST("/v1/post/{id}/unlike")
    suspend fun unlikePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @DELETE("/v1/post/{id}")
    suspend fun deletePost(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Post>
}
