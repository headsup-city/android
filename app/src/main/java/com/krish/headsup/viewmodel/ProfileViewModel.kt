package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Post
import com.krish.headsup.model.User
import com.krish.headsup.repositories.FollowRepository
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
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager,
    private val followRepository: FollowRepository
) : ViewModel() {
    private var userId: String? = null

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _posts = MutableLiveData<Flow<PagingData<Post>>>()
    val posts: LiveData<Flow<PagingData<Post>>> = _posts

    private val _followingUpdates = MutableLiveData<Pair<String, Boolean>>()
    val followingUpdates: LiveData<Pair<String, Boolean>> = _followingUpdates

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _apiError = MutableLiveData<Boolean>()
    val apiError: LiveData<Boolean> = _apiError

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> = _isFollowing

    fun initializeScreen(id: String, selfUser: User?) {
        if (selfUser != null) {
            userId = id
            fetchUserData()
            if (selfUser.following?.contains(id) == true) { _isFollowing.postValue(true) } else { _isFollowing.postValue(false) }
        }
    }

    fun fetchUserData() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val accessToken = tokenManager.getTokenStore()?.access?.token
            val userId = userId
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
            val userId = userId
            if (!accessToken.isNullOrEmpty() && userId != null) {
                try {
                    val postResponse = postRepository.getUserPostStream(accessToken, userId).cachedIn(this)
                    _posts.postValue(postResponse)
                } catch (e: Exception) {
                    _apiError.postValue(true)
                }
            } else {
                _apiError.postValue(true)
            }
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

    suspend fun followUser(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _isFollowing.postValue(false)
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    val response = followRepository.followUser(userId, accessToken)
                    if (response.isSuccessful) {
                        updateUserFollowingList(userId, true)
                        return@withContext true
                    } else {
                        return@withContext false
                    }
                } else {
                    return@withContext false
                }
            } catch (e: Exception) {
                return@withContext false
            }
        }
    }

    suspend fun unFollowUser(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                _isFollowing.postValue(true)
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    val response = followRepository.unFollowUser(userId, accessToken)
                    if (response.isSuccessful) {
                        updateUserFollowingList(userId, false)
                        return@withContext true
                    } else {
                        _isFollowing.postValue(false)
                        return@withContext false
                    }
                } else {
                    _isFollowing.postValue(false)
                    return@withContext false
                }
            } catch (e: Exception) {
                _isFollowing.postValue(false)
                return@withContext false
            }
        }
    }

    private fun updateUserFollowingList(userId: String, follow: Boolean) {
        _followingUpdates.postValue(userId to follow)
    }
}
