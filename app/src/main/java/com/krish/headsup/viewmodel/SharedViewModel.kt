package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.managers.SelfDataManager
import com.krish.headsup.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val selfDataManager: SelfDataManager
) : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    fun fetchUserData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedUser = selfDataManager.getSelf(token)
            fetchedUser?.let {
                _user.postValue(it)
            }
        }
    }

    fun updateUser(updatedUser: User) {
        _user.postValue(updatedUser)
    }
}
