package com.krish.headsup.services.api

import com.krish.headsup.model.GetMessageByConvoIdResType
import com.krish.headsup.model.SendMessageToConvoBodyType
import com.krish.headsup.model.SendMessageToConvoResType
import com.krish.headsup.model.SendMessageToUserBodyType
import com.krish.headsup.model.SendMessageToUserResType
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
    ): Response<GetMessageByConvoIdResType>

    @POST("message/sendMessage/user/{id}")
    suspend fun sendMessageToUser(
        @Path("id") id: String,
        @Body body: SendMessageToUserBodyType,
        @Header("Authorization") token: String
    ): Response<SendMessageToUserResType>

    @POST("message/sendMessage/conversation/{id}")
    suspend fun sendMessageToConversation(
        @Path("id") id: String,
        @Body body: SendMessageToConvoBodyType,
        @Header("Authorization") token: String
    ): Response<SendMessageToConvoResType>
}