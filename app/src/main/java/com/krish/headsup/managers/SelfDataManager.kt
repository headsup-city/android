package com.krish.headsup.managers

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import javax.inject.Inject

class SelfDataManager @Inject constructor(
    private val userRepository: UserRepository
) {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    suspend fun getSelf(token: String): User? {
        Log.d("DebugSelf", "Getting self")
        val response = userRepository.getSelf("Bearer $token")
        Log.d("DebugSelf", "Got self")
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }
}
