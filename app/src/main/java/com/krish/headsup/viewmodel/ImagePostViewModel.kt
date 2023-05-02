package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Comment
import com.krish.headsup.model.Post
import com.krish.headsup.repositories.CommentRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ImagePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tokenManager: TokenManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _post = MutableLiveData<Post>(savedStateHandle.get<Post>("post"))
    private val postId: String = _post.value?.id ?: throw IllegalArgumentException("Missing postId")

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
}
