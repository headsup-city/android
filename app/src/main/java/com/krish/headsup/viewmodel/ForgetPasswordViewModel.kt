package com.krish.headsup.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.ForgotPasswordRequest
import com.krish.headsup.repositories.AuthRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UnitResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val isLoading = MutableLiveData<Boolean>()
    val isSuccessful = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()

    fun forgotPassword(email: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val forgotPasswordRequestBody = ForgotPasswordRequest(email)
                val result = authRepository.forgotPassword(forgotPasswordRequestBody)

                isLoading.value = false

                when (result) {
                    is UnitResult -> {
                        isSuccessful.value = true
                    }
                    is Result.Error -> {
                        errorMessage.value = "Failed to request password recovery"
                    }
                    else -> {
                        errorMessage.value = "Unexpected error occurred while updating profile"
                    }
                }
            } catch (e: Exception) {
                isLoading.value = false
                errorMessage.value = e.message
            }
        }
    }
}
