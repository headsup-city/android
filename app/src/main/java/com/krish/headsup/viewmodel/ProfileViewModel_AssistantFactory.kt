package com.krish.headsup.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.krish.headsup.repositories.CommentRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import javax.inject.Inject

class ProfileViewModel_AssistantFactory  @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) {
    fun create(savedStateHandle: SavedStateHandle): ProfileViewModel {
        return ProfileViewModel(postRepository, tokenManager, savedStateHandle)
    }
}
