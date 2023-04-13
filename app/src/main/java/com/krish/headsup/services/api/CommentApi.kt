package com.krish.headsup.services.api

import com.krish.headsup.model.Comment
import com.krish.headsup.model.CommentForAPostResType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApi {

    @POST("comment/create")
    suspend fun newComment(
        @Header("Authorization") token: String,
        @Query("postId") postId: String,
        @Query("text") text: String
    ): Response<Comment>

    @GET("comment/getCommentForPost/{postId}")
    suspend fun getCommentsForPost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String,
        @Query("page") page: Int? = 0
    ): Response<CommentForAPostResType>

    @POST("comment/{id}/like")
    suspend fun likeComment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    @POST("comment/{id}/unlike")
    suspend fun unlikeComment(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>
}
