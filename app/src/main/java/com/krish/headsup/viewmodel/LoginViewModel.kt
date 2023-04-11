package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.model.LoginRequestBody
import com.krish.headsup.services.api.AuthApi
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences,
    private val tokenManager: TokenManager,
    private val authManager: AuthManager
) : ViewModel() {

    val authState = authManager.authState
    val isLoading = MutableLiveData<Boolean>()

    fun loginUser(emailOrPhone: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                Log.d("loginUser", "Request: \nemailOrPhone:$emailOrPhone, password:$password")
                val loginRequestBody = LoginRequestBody(emailOrPhone, password)
                val response = authApi.login(loginRequestBody)

                Log.d("loginUser", "Response: $response")

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        userPreferences.saveUser(loginResponse.user)
                        tokenManager.saveTokens(loginResponse.tokens)
                        isLoading.value = false
                        authManager.updateAuthState(AuthState.AUTHENTICATED)
                    }
                } else {
                    isLoading.value = false
                    authManager.updateAuthState(AuthState.NO_USER)
                }
            } catch (e: Exception) {
                isLoading.value = false
                authManager.updateAuthState(AuthState.NO_USER)
            }
        }
    }
}
