package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager,
    private val followRepository: FollowRepository,
    private val sharedViewModel: SharedViewModel,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId = savedStateHandle.get<String>("userId")

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _posts = MutableLiveData<Flow<PagingData<Post>>>()
    val posts: LiveData<Flow<PagingData<Post>>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _isLoading.postValue(true)
            val accessToken = tokenManager.getTokenStore()?.access?.token
            if (!accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {

                val userResponse = userRepository.getUser(accessToken, userId)
                if (userResponse.isSuccessful) {
                    _user.postValue(userResponse.body())
                    fetchUserPosts()
                }
            }
            _isLoading.postValue(false)
        }
    }

    private fun fetchUserPosts(): Flow<PagingData<Post>> {
        val accessToken = tokenManager.getTokenStore()?.access?.token
        if (!accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
            val postResponse = postRepository.getUserPostStream(accessToken, userId)
                .cachedIn(viewModelScope)
            _posts.value = postResponse
            return postResponse
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
                        is Result.Success -> {
                            return@withContext true
                        }
                        is Result.Error -> {
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

    suspend fun unlikePost(postId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !postId.isNullOrEmpty()) {
                    when (postRepository.unlikePost(accessToken, postId)) {
                        is Result.Success -> {
                            return@withContext true
                        }
                        is Result.Error -> {
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

    suspend fun followUser(userId: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
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
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty() && !userId.isNullOrEmpty()) {
                    val response = followRepository.unFollowUser(userId, accessToken)
                    if (response.isSuccessful) {
                        updateUserFollowingList(userId, false)
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

    private fun updateUserFollowingList(userId: String, follow: Boolean) {
        // Get the current user from sharedViewModel.user LiveData
        val currentUser = sharedViewModel.user.value

        if (currentUser != null) {
            // Update the following list depending on the 'follow' flag
            val updatedFollowingList = if (follow) {
                currentUser.following?.toMutableSet()?.apply { add(userId) }?.toList()
            } else {
                currentUser.following?.toMutableSet()?.apply { remove(userId) }?.toList()
            }

            // Update the sharedViewModel's user LiveData with the updated following list
            sharedViewModel.updateUser(currentUser.copy(following = updatedFollowingList))
        }
    }
}
