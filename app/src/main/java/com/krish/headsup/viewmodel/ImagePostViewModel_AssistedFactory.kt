package com.krish.headsup.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.krish.headsup.repositories.CommentRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import javax.inject.Inject

class ImagePostViewModel_AssistedFactory @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tokenManager: TokenManager
) {
    fun create(savedStateHandle: SavedStateHandle): ImagePostViewModel {
        return ImagePostViewModel(postRepository, commentRepository, tokenManager, savedStateHandle)
    }
}
