package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krish.headsup.model.User
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.TokenManager
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
                    try {
                        val response = userRepository.searchUser(accessToken, query)
                        if (response.isSuccessful && response.body() != null) {
                            searchResults.value = response.body()!!.results
                        } else {
                            throw IllegalStateException("Search request failed with ${response.code()}")
                        }
                    } catch (e: Exception) {
                        // You can emit an error here which can be caught by the UI to display an error message
                        searchResults.value = emptyList<User>()
                        Log.e("Debug", "Error occurred while searching: ${e.localizedMessage}")
                    }
                }
            }
        } else {
            throw IllegalStateException("Access token is missing")
        }
    }
}
