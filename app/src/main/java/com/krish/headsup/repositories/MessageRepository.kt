package com.krish.headsup.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.Message
import com.krish.headsup.model.SendMessageToConversationRequest
import com.krish.headsup.model.SendMessageToUserRequest
import com.krish.headsup.paging.MessagingPagingSource
import com.krish.headsup.services.api.MessageApi
import kotlinx.coroutines.flow.Flow
import java.io.IOException
import javax.inject.Inject

class MessageRepository @Inject constructor(private val messageApi: MessageApi) {

    fun getMessagesByConvoId(
        convoId: String,
        accessToken: String,
    ): Flow<PagingData<Message>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = 200,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = { MessagingPagingSource(messageApi, convoId, accessToken) }
        ).flow
    }

    suspend fun sendMessageToUser(userId: String, message: String, accessToken: String): Result<Boolean> {
        return try {
            val request = SendMessageToUserRequest(message)
            val response = messageApi.sendMessageToUser(userId, request, accessToken)
            if (response.isSuccessful) Result.success(true) else Result.failure(IOException("Error sending message to user"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessageToConversation(convoId: String, message: String, accessToken: String): Result<Boolean> {
        return try {
            val request = SendMessageToConversationRequest(message)
            val response = messageApi.sendMessageToConversation(convoId, request, accessToken)
            if (response.isSuccessful) Result.success(true) else Result.failure(IOException("Error sending message to conversation"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
