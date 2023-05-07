package com.krish.headsup.viewmodel

import android.util.Log
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
                    when (postRepository.likePost(accessToken, postId)) {
                        is Result.Success-> {
                            Log.d("DebugSelf", "Emitting likePost true")
                            return@withContext true
                        }
                        is Result.Error -> {
                            Log.d("DebugSelf", "Emitting likePost not successful api false")
                            return@withContext false
                        }
                    }
                } else {
                    Log.d("DebugSelf", "Emitting likePost else false")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.d("DebugSelf", "Emitting likePost catch false: ${e.message}")
                return@withContext false
            }
        }
    }

    suspend fun unlikePost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !postId.isNullOrEmpty()) {
                    when (postRepository.unlikePost(accessToken, postId)) {
                        is Result.Success-> {
                            Log.d("DebugSelf", "Emitting unlikePost true")
                            return@withContext true
                        }
                        is Result.Error -> {
                            Log.d("DebugSelf", "Emitting unlikePost not successful api false")
                            return@withContext false
                        }
                    }
                } else {
                    Log.d("DebugSelf", "Emitting unlikePost else false")
                    return@withContext false
                }
            } catch (e: Exception) {
                Log.d("DebugSelf", "Emitting unlikePost catch false: ${e.message}")
                return@withContext false
            }
        }
    }
}
