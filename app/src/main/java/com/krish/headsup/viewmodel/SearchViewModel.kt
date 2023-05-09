package com.krish.headsup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: String
        get() = _query.value

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun searchUser(query: String): Flow<PagingData<User>> {
        val accessToken = tokenManager.getTokenStore()?.access?.token
        if (accessToken != null) {
            return userRepository.searchUserStream(accessToken, query)
        } else {
            throw IllegalStateException("Access token is missing")
        }
    }
}
