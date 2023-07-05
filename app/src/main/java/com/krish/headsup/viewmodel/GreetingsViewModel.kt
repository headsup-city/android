package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.model.GoogleSignInRequest
import com.krish.headsup.model.SignUpSource
import com.krish.headsup.repositories.AuthRepository
import com.krish.headsup.utils.LoginResult
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GreetingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val tokenManager: TokenManager,
    private val authManager: AuthManager
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _apiError = MutableLiveData<Boolean>()
    val apiError: LiveData<Boolean> = _apiError

    fun signInWithGoogle(accessToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            _apiError.postValue(false)
            try {
                val signUpSource = SignUpSource("android", "mobile")
                val signInRequest = GoogleSignInRequest(accessToken, signUpSource)
                when (val signInResult = authRepository.signinWithGoogle(signInRequest)) {
                    is LoginResult -> {
                        userPreferences.saveUser(signInResult.data.user)
                        tokenManager.saveTokens(signInResult.data.tokens)
                        authManager.updateAuthState(AuthState.AUTHENTICATED)
                    }
                    else -> {
                        _apiError.postValue(true)
                    }
                }
            } catch (e: Exception) {
                _apiError.postValue(true)
            }
            _isLoading.postValue(false)
        }
    }
}
