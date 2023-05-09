package com.krish.headsup.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.headsup.model.User
import com.krish.headsup.services.api.UserApi
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val userApi: UserApi,
    private val accessToken: String,
    private val query: String
) : PagingSource<Int, User>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        val nextPage = params.key ?: 0

        return try {
            val response = userApi.searchUser(accessToken, query, nextPage)
            val data = response.body()?.results ?: listOf()
            val totalPages = response.body()?.totalPages ?: 0
            val prevKey = if (nextPage > 0) nextPage - 1 else null
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

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
