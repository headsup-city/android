package com.krish.headsup.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.krish.headsup.repositories.FollowRepository
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.TokenManager
import javax.inject.Inject

class ProfileViewModel_AssistedFactory @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val tokenManager: TokenManager,
    private val followRepository: FollowRepository,
) {
    fun create(savedStateHandle: SavedStateHandle, sharedViewModel: SharedViewModel): ProfileViewModel {
        return ProfileViewModel(userRepository, postRepository, tokenManager, followRepository, sharedViewModel, savedStateHandle)
    }
}
