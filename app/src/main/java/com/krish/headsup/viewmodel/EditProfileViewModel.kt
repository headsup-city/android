package com.krish.headsup.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.UpdatePasswordRequest
import com.krish.headsup.model.UpdateUserRequest
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.AvatarResult
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val tokenManager: TokenManager,
    private val application: Application
) : ViewModel() {
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    val errorMessage = MutableLiveData<String?>()

    val successMessage = MutableLiveData<String?>()

    private val _notifyProfileUpdate = MutableLiveData<Boolean>(false)
    val notifyProfileUpdate: LiveData<Boolean> get() = _notifyProfileUpdate

    fun initializeScreen(selfUser: User?) {
        if (selfUser != null) {
            _user.postValue(selfUser)
        }
    }

    fun updateUserProfile(newName: String, newPhoneNumber: String) {
        viewModelScope.launch {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                val currentUser = user.value
                if (!accessToken.isNullOrEmpty() && currentUser != null) {
                    val request = UpdateUserRequest(name = newName, email = null, mobileNumber = newPhoneNumber, password = null)
                    when (val result = userRepository.updateUser(accessToken, currentUser.id, request)) {
                        is UserResult -> {
                            successMessage.value = "Profile updated successfully"
                            _notifyProfileUpdate.postValue(true)
                        }
                        is Result.Error -> {
                            errorMessage.value = result.exception.message
                        }
                        else -> {
                            errorMessage.value = "Unexpected error occurred while updating profile"
                        }
                    }
                } else {
                    errorMessage.value = "Failed to update profile: Missing user or access token"
                }
            } catch (e: Exception) {
                errorMessage.value = "Something went wrong, please try again."
//            _saveProfileButtonEnabled.value = true
            }
        }
    }

    fun updateAvatar(imageUri: Uri) {
        viewModelScope.launch {
            val context = application.applicationContext
            try {
                val cursor = context.contentResolver.query(imageUri, null, null, null, null)
                val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor?.moveToFirst()
                val name = cursor?.getString(nameIndex!!) ?: "default_name"
                cursor?.close()

                val file = File(context.cacheDir, name)

                withContext(Dispatchers.IO) {
                    val os = FileOutputStream(file).buffered()
                    os.use {
                        val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    }
                }

                val body = MultipartBody.Part.createFormData(
                    "image",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaType())
                )
                val accessToken = tokenManager.getTokenStore()?.access?.token
                val currentUser = user.value
                if (!accessToken.isNullOrEmpty() && currentUser != null) {

                    val result = userRepository.updateAvatar(accessToken, currentUser.id, body)

                    // update liveData with the new user data
                    when (result) {
                        is AvatarResult -> {
                            file.delete()
                            successMessage.value = "Profile picture updated successfully"
                            _notifyProfileUpdate.postValue(true)
                        }
                        is Result.Error -> {
                            errorMessage.value = result.exception.message
                        }
                        else -> {
                            errorMessage.value = "Unexpected error occurred while updating profile"
                        }
                    }
                } else { errorMessage.value = "Failed to update profile, missing user or access token" }
            } catch (e: Exception) { errorMessage.value = "Failed to update profile, ${e.message}" }
        }
    }

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {

                val accessToken = tokenManager.getTokenStore()?.access?.token
                val currentUser = user.value
                if (!accessToken.isNullOrEmpty() && currentUser != null) {
                    val updatePasswordRequest = UpdatePasswordRequest(oldPassword, newPassword)
                    val result = userRepository.changePassword(accessToken, currentUser.id, updatePasswordRequest)

                    when (result) {
                        is UserResult -> {
                            successMessage.value = "Password updated successfully"
                        }
                        is Result.Error -> {
                            // Handle error
                            errorMessage.postValue(result.exception.message)
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                errorMessage.postValue("Failed to update password")
            }
        }
    }

    fun clearErrorMessage() {
        errorMessage.value = null
    }

    fun clearSuccessMessage() {
        successMessage.value = null
    }

    fun resetNotifyProfileUpdate() {
        _notifyProfileUpdate.value = false
    }
}
