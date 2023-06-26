package com.krish.headsup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UnitResult
import com.krish.headsup.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
    private val authManager: AuthManager,
    private val userPreferences: UserPreferences
) : ViewModel() {
    val errorMessage = MutableLiveData<String?>()

    fun deleteUserAccount() {
        viewModelScope.launch {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty()) {
                    when (val result = userRepository.deleteUser(accessToken)) {
                        is UnitResult -> {
                            // Clean local user data and tokens
                            authManager.updateAuthState(AuthState.NO_USER)
                            tokenManager.clearTokens()
                            userPreferences.deleteUser()
                        }
                        is Result.Error -> {
                            errorMessage.value = result.exception.message
                        }
                        else -> {
                            errorMessage.value = "Unexpected error occurred while deleting account"
                        }
                    }
                } else {
                    errorMessage.value = "Failed to delete account: Missing user or access token"
                }
            } catch (e: Exception) {
                errorMessage.value = "Something went wrong, please try again."
            }
        }
    }
}
