package com.krish.headsup.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.ui.DeepLinkFragmentDirections
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeepLinkViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _navigationCommand = MutableLiveData<NavDirections?>()
    val navigationCommand: LiveData<NavDirections?> = _navigationCommand

    var isDeepLinkProcessed = false
        private set

    fun onNavigationCommandHandled() {
        _navigationCommand.value = null
    }

    fun handleDeepLink(uri: Uri) {
        val postId = uri.lastPathSegment
        if (postId != null) {
            viewModelScope.launch {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (accessToken != null) {
                    postRepository.getPostById(accessToken, postId)?.let { post ->
                        when (post.postType) {
                            "PRIMARY" -> {
                                if (post.attachment?.uri.isNullOrEmpty()) {
                                    _navigationCommand.value =
                                        DeepLinkFragmentDirections.actionDeepLinkFragmentToTextPostFragment(
                                            post
                                        )
                                } else {
                                    _navigationCommand.value = DeepLinkFragmentDirections.actionDeepLinkFragmentToImagePostFragment(post)
                                }
                            }
                            "SHORT" -> _navigationCommand.value = DeepLinkFragmentDirections.actionDeepLinkFragmentToVideoPostFragment(post)
                        }
                        isDeepLinkProcessed = true
                    }
                }
            }
        }
    }
}
