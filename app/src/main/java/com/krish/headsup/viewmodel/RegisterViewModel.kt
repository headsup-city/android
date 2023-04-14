package com.krish.headsup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.AuthState
import com.krish.headsup.model.RegistrationRequest
import com.krish.headsup.services.api.AuthApi
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authApi: AuthApi,
    private val userPreferences: UserPreferences,
    private val tokenManager: TokenManager
) : ViewModel() {

    val authState = MutableLiveData<AuthState>()

    fun registerUser(name: String, email: String, password: String) {
        authState.value = AuthState.LOADING

        val signUpSource = mapOf("mobile" to "android")

        viewModelScope.launch {
            try {
                val registerRequestBody = RegistrationRequest(name, email, password, signUpSource)
                val response = authApi.register(registerRequestBody)

                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        userPreferences.saveUser(loginResponse.user)
                        tokenManager.saveTokens(loginResponse.tokens)
                        authState.value = AuthState.AUTHENTICATED
                    }
                } else {
                    authState.value = AuthState.NO_USER
                }
            } catch (e: Exception) {
                authState.value = AuthState.NO_USER
            }
        }
    }
}
