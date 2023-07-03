package com.krish.headsup.repositories

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.krish.headsup.model.Post
import com.krish.headsup.paging.PostPagingSource
import com.krish.headsup.paging.UserPostPagingSource
import com.krish.headsup.services.api.PostApi
import com.krish.headsup.services.api.ReportApi
import com.krish.headsup.services.api.ReportPostArgsType
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UnitResult
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import javax.inject.Inject

class PostRepository @Inject constructor(private val postApi: PostApi, private val reportApi: ReportApi) {

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

    suspend fun createPrimaryPost(token: String, body: MultipartBody): Result {
        return try {
            val response = postApi.uploadPrimaryPost(token, body)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to create primary post"))
            }
        } catch (e: Exception) {
            Log.d("DebugSelf", e.message.toString())
            Result.Error(e)
        }
    }

    suspend fun likePost(accessToken: String, postId: String): Result {
        return try {
            val response = postApi.likePost("Bearer $accessToken", postId)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("API request failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun unlikePost(accessToken: String, postId: String): Result {
        return try {
            val response = postApi.unlikePost("Bearer $accessToken", postId)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("API request failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun reportPost(accessToken: String, postId: String): Result {
        val requestBody = ReportPostArgsType(postId)
        return try {
            val response = reportApi.reportPost(requestBody, "Bearer $accessToken")
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("API request failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
