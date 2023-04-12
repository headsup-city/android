package com.krish.headsup.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krish.headsup.model.AuthState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthManager @Inject constructor() {
    private val _authState = MutableLiveData<AuthState>().apply {
        value = AuthState.LOADING
    }

    val authState: LiveData<AuthState> = _authState

    fun updateAuthState(newState: AuthState) {
        _authState.postValue(newState)
    }
}
