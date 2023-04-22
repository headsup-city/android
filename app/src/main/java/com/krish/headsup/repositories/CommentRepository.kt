package com.krish.headsup.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.Comment
import com.krish.headsup.paging.CommentPagingSource
import com.krish.headsup.services.api.CommentApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CommentRepository @Inject constructor(private val commentApi: CommentApi) {

    fun getCommentsForPostStream(accessToken: String, postId: String): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = 200,
                prefetchDistance = 3
            ),
            pagingSourceFactory = { CommentPagingSource(commentApi, accessToken, postId) }
        ).flow
    }
}
