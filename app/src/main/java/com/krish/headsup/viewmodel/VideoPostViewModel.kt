package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Comment
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.CommentRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tokenManager: TokenManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val postId: String = savedStateHandle["postId"] ?: throw IllegalArgumentException("Missing postId")

    private val _post = MutableLiveData<Post>()
    val post: LiveData<Post>
        get() = _post

    val comments: Flow<PagingData<Comment>>
        get() {
            val accessToken = tokenManager.getTokenStore()?.access?.token
            if (accessToken != null) {
                return commentRepository.getCommentsForPostStream(accessToken, postId).cachedIn(viewModelScope)
            } else {
                throw IllegalStateException("Access token is missing")
            }
        }

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token
            if (accessToken != null) {
                postRepository.getPostById(accessToken, postId)?.let { post ->
                    _post.value = post
                }
            } else {
                throw IllegalStateException("Access token is missing")
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(postId: String): VideoPostViewModel
    }
}
