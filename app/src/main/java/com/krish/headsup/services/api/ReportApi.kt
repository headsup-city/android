package com.krish.headsup.services.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class ReportPostArgsType(val postId: String)
data class ReportCommentArgsType(val commentId: String)

interface ReportApi {

    @POST("report/post")
    suspend fun reportPost(
        @Body args: ReportPostArgsType,
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("report/comment")
    suspend fun reportComment(
        @Body args: ReportCommentArgsType,
        @Header("Authorization") token: String
    ): Response<Unit>
}
