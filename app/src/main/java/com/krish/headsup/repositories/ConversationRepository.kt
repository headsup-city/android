package com.krish.headsup.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.paging.ConversationPagingSource
import com.krish.headsup.services.api.ConversationApi
import com.krish.headsup.utils.ConversationResult
import com.krish.headsup.utils.Result
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ConversationRepository @Inject constructor(private val conversationApi: ConversationApi) {

    suspend fun getConvoById(token: String, id: String): Result {
        return try {
            val response = conversationApi.getConvoById(token, id)
            if (response.isSuccessful && response.body() != null) {
                ConversationResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to fetch conversation by ID"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getConvoByUserId(token: String, userId: String): Result {
        return try {
            val response = conversationApi.getConvoByUserId(token, userId)
            if (response.isSuccessful && response.body() != null) {
                ConversationResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to fetch conversation by user ID"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun getConversations(token: String): Flow<PagingData<ConversationFull>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = 200,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = { ConversationPagingSource(conversationApi, token) }
        ).flow
    }
}
