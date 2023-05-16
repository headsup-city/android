package com.krish.headsup.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.Message
import com.krish.headsup.model.SendMessageToConversationRequest
import com.krish.headsup.model.SendMessageToUserRequest
import com.krish.headsup.paging.MessagingPagingSource
import com.krish.headsup.services.api.MessageApi
import com.krish.headsup.utils.MessagesResult
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.SendMessageToConversationResult
import com.krish.headsup.utils.SendMessageToUserResult
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

    suspend fun getInitialMessagesByConvoId(convoId: String, accessToken: String): Result {
        return try {
            val response = messageApi.getMessageByConvoId(convoId, 0, accessToken)
            if (response.isSuccessful && response.body() != null) {
                MessagesResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to fetch messages by conversation ID"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendMessageToUser(userId: String, message: String, accessToken: String): Result {
        return try {
            val request = SendMessageToUserRequest(message)
            val response = messageApi.sendMessageToUser(userId, request, accessToken)
            if (response.isSuccessful && response.body() != null) {
                SendMessageToUserResult(response.body()!!)
            } else {
                Result.Error(IOException("Error sending message to user"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendMessageToConversation(convoId: String, message: String, accessToken: String): Result {
        return try {
            val request = SendMessageToConversationRequest(message)
            val response = messageApi.sendMessageToConversation(convoId, request, accessToken)
            if (response.isSuccessful && response.body() != null) {
                SendMessageToConversationResult(response.body()!!)
            } else {
                Result.Error(IOException("Error sending message to conversation"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
