package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UnitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus> get() = _loadingStatus

    fun createPrimaryPost(body: MultipartBody) = viewModelScope.launch { // Change the function signature to accept MultipartBody
        _loadingStatus.value = LoadingStatus.Loading
        val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""

        val result = postRepository.createPrimaryPost(accessToken, body) // Use the MultipartBody as is

        when (result) {
            is UnitResult -> {
                _loadingStatus.value = LoadingStatus.Success
            }
            is Result.Error -> {
                _loadingStatus.value = LoadingStatus.Error
            }
            else -> { _loadingStatus.value = LoadingStatus.Error }
        }
    }

    enum class LoadingStatus {
        Loading,
        Success,
        Error,
    }
}
