package com.krish.headsup.viewmodel

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.krish.headsup.repositories.CommentRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.utils.TokenManager
import javax.inject.Inject

class VideoPostViewModelFactory @Inject constructor(
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tokenManager: TokenManager,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        if (modelClass.isAssignableFrom(VideoPostViewModel::class.java)) {
            return VideoPostViewModel(postRepository, commentRepository, tokenManager, handle) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
