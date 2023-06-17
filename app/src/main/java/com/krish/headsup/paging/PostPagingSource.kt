package com.krish.headsup.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.headsup.model.Post
import com.krish.headsup.services.api.PostApi
import retrofit2.HttpException
import java.io.IOException

class PostPagingSource(
    private val postApi: PostApi,
    private val accessToken: String,
    private val latitude: Double,
    private val longitude: Double,
) : PagingSource<Int, Post>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val nextPage = params.key ?: 0

        return try {
            val response = postApi.getGeneralPost(accessToken, nextPage, latitude, longitude)
            val data = response.body()?.results?.filter { it.postType != "SHORT" } ?: emptyList()
            val totalPages = response.body()?.totalPages ?: 0
            val prevKey = if (nextPage > 0) nextPage - 1 else null
            val nextKey = if (totalPages - 1 > nextPage) nextPage + 1 else null

            LoadResult.Page(
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
                itemsAfter = if (data.isEmpty()) 0 else LoadResult.Page.COUNT_UNDEFINED
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
