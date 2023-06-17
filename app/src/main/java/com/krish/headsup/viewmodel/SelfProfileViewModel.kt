package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Post
import com.krish.headsup.model.User
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UnitResult
import com.krish.headsup.utils.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SelfProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _posts = MutableLiveData<Flow<PagingData<Post>>>()
    val posts: LiveData<Flow<PagingData<Post>>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _apiError = MutableLiveData<Boolean>()
    val apiError: LiveData<Boolean> = _apiError

    fun initializeScreen(initUser: User?) {
        initUser?.let { user ->
            _user.postValue(user)
            fetchUserData(initUser?.id)
        }
    }

    fun retry() {
        fetchUserData(user.value?.id)
    }

    private fun fetchUserData(userId: String?) {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val accessToken = tokenManager.getTokenStore()?.access?.token
            if (!accessToken.isNullOrEmpty() && userId != null) {
                when (val userResponse = userRepository.getUser(accessToken, userId)) {
                    is UserResult -> {
                        _user.postValue(userResponse.data)
                        fetchUserPosts()
                    }
                    is Result.Error -> {
                        _apiError.postValue(true)
                    }
                    else -> {
                        _apiError.postValue(true)
                    }
                }
            } else {
                _apiError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }

    private fun fetchUserPosts() {
        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token
            val userId = user.value?.id
            if (!accessToken.isNullOrEmpty() && userId != null) {
                val postResponse = postRepository.getUserPostStream(accessToken, userId).cachedIn(this)
                _posts.postValue(postResponse)
            } else {
                _apiError.postValue(true)
            }
        }
    }

    suspend fun likePost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && postId.isNotEmpty()) {
                    when (postRepository.likePost(accessToken, postId)) {
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
                if (!accessToken.isNullOrEmpty() && postId.isNotEmpty()) {
                    when (postRepository.unlikePost(accessToken, postId)) {
                        is UnitResult -> {
                            return@withContext true
                        }
                        is Result.Error -> {
                            return@withContext false
                        }
                        else -> {
                            return@withContext false
                        }
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
