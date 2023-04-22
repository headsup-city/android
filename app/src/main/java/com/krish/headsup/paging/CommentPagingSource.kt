package com.krish.headsup.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.krish.headsup.model.Comment
import com.krish.headsup.services.api.CommentApi
import retrofit2.HttpException
import java.io.IOException

class CommentPagingSource(
    private val commentApi: CommentApi,
    private val accessToken: String,
    private val postId: String
) : PagingSource<Int, Comment>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val page = params.key ?: 0

        return try {
            val response = commentApi.getCommentsForPost("Bearer $accessToken", postId, page)
            val comments = response.body()?.results ?: emptyList()
            val nextPage = if (comments.isEmpty()) null else page + 1

            LoadResult.Page(
                data = comments,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
