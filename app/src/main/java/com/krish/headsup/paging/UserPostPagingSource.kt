package com.krish.headsup.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.headsup.model.Post
import com.krish.headsup.services.api.PostApi
import retrofit2.HttpException
import java.io.IOException

class UserPostPagingSource(
    private val postApi: PostApi,
    private val accessToken: String,
    private val userId: String,
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val nextPage = params.key ?: 0

        return try {
            val response = postApi.getPostsForUser(accessToken, userId, nextPage)
            val data = response.body()?.results?.filter { it.postType != "SHORT" } ?: emptyList()
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

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
