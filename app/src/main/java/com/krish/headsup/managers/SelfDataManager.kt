package com.krish.headsup.managers

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UserResult
import javax.inject.Inject

class SelfDataManager @Inject constructor(
    private val userRepository: UserRepository
) {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    suspend fun getSelf(token: String): User? {
        return when (val response = userRepository.getSelf("Bearer $token")) {
            is UserResult -> response.data
            is Result.Error -> null
            else -> {
                null
            }
        }
    }
}
