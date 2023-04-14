package com.krish.headsup.services.api

import com.krish.headsup.model.GetMessagesByConversationIdResponse
import com.krish.headsup.model.SendMessageToConversationRequest
import com.krish.headsup.model.SendMessageToConversationResponse
import com.krish.headsup.model.SendMessageToUserRequest
import com.krish.headsup.model.SendMessageToUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessageApi {

    @GET("message/conversation/{convoId}")
    suspend fun getMessageByConvoId(
        @Path("convoId") convoId: String,
        @Query("page") page: Int,
        @Header("Authorization") token: String
    ): Response<GetMessagesByConversationIdResponse>

    @POST("message/sendMessage/user/{id}")
    suspend fun sendMessageToUser(
        @Path("id") id: String,
        @Body body: SendMessageToUserRequest,
        @Header("Authorization") token: String
    ): Response<SendMessageToUserResponse>

    @POST("message/sendMessage/conversation/{id}")
    suspend fun sendMessageToConversation(
        @Path("id") id: String,
        @Body body: SendMessageToConversationRequest,
        @Header("Authorization") token: String
    ): Response<SendMessageToConversationResponse>
}
