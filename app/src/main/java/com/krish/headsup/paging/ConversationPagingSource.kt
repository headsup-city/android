package com.krish.headsup.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.services.api.ConversationApi
import retrofit2.HttpException
import java.io.IOException

class ConversationPagingSource(
    private val conversationApi: ConversationApi,
    private val accessToken: String,
) : PagingSource<Int, ConversationFull>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ConversationFull> {
        val nextPage = params.key ?: 0

        return try {
            val response = conversationApi.getConversations(accessToken, nextPage)
            val data = response.body()?.results ?: emptyList()
            val totalPages = response.body()?.totalPages ?: 0
            val prevKey = null
            val nextKey = if (totalPages - 1 > nextPage) nextPage + 1 else null

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ConversationFull>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
