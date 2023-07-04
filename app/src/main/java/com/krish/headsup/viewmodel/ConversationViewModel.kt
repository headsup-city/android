package com.krish.headsup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.repositories.ConversationRepository
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val conversationRepository: ConversationRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    var conversations: Flow<PagingData<ConversationFull>>? = null
        private set

    fun getConversations() {
        val token = tokenManager.getTokenStore()?.access?.token ?: return
        conversations = conversationRepository.getConversations(token).cachedIn(viewModelScope)
    }
}
