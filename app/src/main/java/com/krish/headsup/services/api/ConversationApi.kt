package com.krish.headsup.services.api

import com.krish.headsup.model.Conversation
import com.krish.headsup.model.ConversationListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface ConversationApi {
    @GET("/v1/conversation/{id}")
    suspend fun getConvoById(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Conversation>

    @GET("/v1/conversation/with-user/{userId}")
    suspend fun getConvoByUserId(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<Conversation>

    @GET("/v1/conversation/user/all-conversations")
    suspend fun getConversations(
        @Header("Authorization") token: String,
        @Query("page") page: Int? = 0
    ): Response<ConversationListResponse>
}
