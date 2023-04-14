package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    private val _posts = MutableLiveData<Resource<List<Post>>>()
    val posts: LiveData<Resource<List<Post>>> = _posts

    fun loadPosts() {
        viewModelScope.launch {
            _posts.value = Resource.Loading()
            val result = postRepository.getGeneralPost()
            _posts.value = result
        }
    }
}
