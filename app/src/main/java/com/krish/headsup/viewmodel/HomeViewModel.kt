package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UnitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _currentPostResult = MutableLiveData<Flow<PagingData<Post>>>()
    val currentPostResult: LiveData<Flow<PagingData<Post>>> = _currentPostResult

    fun loadPosts(latitude: Double, longitude: Double): Flow<PagingData<Post>> {
        val accessToken = tokenManager.getTokenStore()?.access?.token
        if (accessToken != null) {
            val newResult = postRepository.getGeneralPostStream(accessToken, latitude, longitude)
                .cachedIn(viewModelScope)
            _currentPostResult.value = newResult
            return newResult
        } else {
            throw IllegalStateException("Access token is missing")
        }
    }

    suspend fun likePost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !postId.isNullOrEmpty()) {
                    val result = postRepository.likePost(accessToken, postId)
                    when (result) {
                        is UnitResult -> {
                            return@withContext true
                        }
                        is Result.Error -> {
                            return@withContext false
                        }
                        else -> { return@withContext false }
                    }
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    suspend fun unlikePost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !postId.isNullOrEmpty()) {
                    val result = postRepository.unlikePost(accessToken, postId)
                    when (result) {
                        is UnitResult -> {
                            return@withContext true
                        }
                        is Result.Error -> {
                            return@withContext false
                        }
                        else -> { return@withContext false }
                    }
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    suspend fun reportPost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && postId.isNotEmpty()) {
                    val result = postRepository.reportPost(accessToken, postId)
                    when (result) {
                        is UnitResult -> {
                            return@withContext true
                        }
                        is Result.Error -> {
                            return@withContext false
                        }
                        else -> { return@withContext false }
                    }
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }
}
