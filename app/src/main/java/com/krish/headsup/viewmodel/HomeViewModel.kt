package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _currentPostResult = MutableLiveData<Flow<PagingData<Post>>>()
    val currentPostResult: LiveData<Flow<PagingData<Post>>> = _currentPostResult

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun loadPosts(latitude: Double, longitude: Double, reset: Boolean = false): Flow<PagingData<Post>> {
        val accessToken = tokenManager.getTokenStore()?.access?.token
        if (accessToken != null) {
            _isLoading.value = true // Set loading flag to true
            val newResult = postRepository.getGeneralPostStream(accessToken, latitude, longitude, reset = reset)
                .cachedIn(viewModelScope)
            _currentPostResult.value = newResult
            _isLoading.value = false // Set loading flag to false after loading is done
            return newResult
        } else {
            throw IllegalStateException("Access token is missing")
        }
    }

}
