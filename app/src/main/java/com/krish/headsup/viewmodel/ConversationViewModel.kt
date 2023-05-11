package com.krish.headsup.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.repositories.ConversationRepository
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val repository: ConversationRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    var conversations: Flow<PagingData<ConversationFull>>? = null
        private set

    fun getConversations() {
        val token = tokenManager.getTokenStore()?.access?.token ?: return
        conversations = repository.getConversations(token).cachedIn(viewModelScope)
    }
}

