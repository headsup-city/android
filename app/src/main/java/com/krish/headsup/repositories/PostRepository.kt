package com.krish.headsup.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.Post
import com.krish.headsup.paging.PostPagingSource
import com.krish.headsup.paging.UserPostPagingSource
import com.krish.headsup.services.api.PostApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApi: PostApi) {

    private var postPagingSource: PostPagingSource? = null
    private var userPostPagingSource: UserPostPagingSource? = null

    fun getGeneralPostStream(accessToken: String, latitude: Double, longitude: Double): Flow<PagingData<Post>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = 200,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                PostPagingSource(postApi, accessToken, latitude, longitude).also {
                    postPagingSource = it
                }
            }
        ).flow
    }

    suspend fun getPostById(accessToken: String, id: String): Post? {
        return try {
            val response = postApi.getPost("Bearer $accessToken", id)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getUserPostStream(accessToken: String, userId: String): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 40,
                maxSize = 200,
                prefetchDistance = 3,
            ),
            pagingSourceFactory = {
                UserPostPagingSource(postApi, accessToken, userId).also {
                    userPostPagingSource = it
                }
            }
        ).flow
    }
}
