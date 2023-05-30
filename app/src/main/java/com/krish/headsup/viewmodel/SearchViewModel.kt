package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserResult
import com.krish.headsup.utils.UserSearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")

    // Replace with a state flow of List<User>
    val searchResults = MutableStateFlow<List<User>>(emptyList())

    val query: String
        get() = _query.value

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
        viewModelScope.launch {
            searchUser(newQuery)
        }
    }

    private fun searchUser(query: String) {
        val accessToken = tokenManager.getTokenStore()?.access?.token
        if (accessToken != null) {
            if (query.isBlank()) {
                searchResults.value = emptyList<User>()
            } else {
                viewModelScope.launch {
                     when (val response = userRepository.searchUser(accessToken, query)) {
                        is UserSearchResult -> {
                            searchResults.value=response.data.results
                        }
                        is Result.Error-> {
                            // You can emit an error here which can be caught by the UI to display an error message
                            searchResults.value = emptyList<User>()
                            Log.e("SelfDebug", "Error occurred while searching")
                        }
                        else -> {
                            // You can emit an error here which can be caught by the UI to display an error message
                            searchResults.value = emptyList<User>()
                            Log.e("SelfDebug", "Error occurred while searching")
                        }
                    }
                }
            }
        } else {
            throw IllegalStateException("Access token is missing")
        }
    }
}
