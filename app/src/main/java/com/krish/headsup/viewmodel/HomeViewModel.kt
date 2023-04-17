package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.Resource
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    fun loadPosts(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            val accessToken = tokenManager.getTokenStore()?.access?.token
            Log.d("HomeViewLogAccessToken", "$accessToken")
            if (accessToken != null) {
                val result = postRepository.getGeneralPost(accessToken, 0, latitude, longitude)
                Log.d("HomeViewLogResult", "$result")
                _posts.value = result
            } else {
                _posts.value = Resource.error("Access token is missing")
            }
        }
    }
}
