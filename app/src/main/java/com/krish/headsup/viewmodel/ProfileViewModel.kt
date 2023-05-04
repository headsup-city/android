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
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager,
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
}
