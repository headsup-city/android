package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.krish.headsup.model.Message
import com.krish.headsup.repositories.MessageRepository
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    private var currentMessageResult: Flow<PagingData<Message>>? = null

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun getMessages(convoId: String): Flow<PagingData<Message>> {
        val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
        val newResult: Flow<PagingData<Message>> =
            messageRepository.getMessagesByConvoId(convoId, accessToken)
                .cachedIn(viewModelScope)
        currentMessageResult = newResult
        return newResult
    }

    fun sendMessageToUser(userId: String, message: String) {
        viewModelScope.launch {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
                messageRepository.sendMessageToUser(userId, message, accessToken)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }

    fun sendMessageToConversation(convoId: String, message: String) {
        viewModelScope.launch {
            try {
                val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
                messageRepository.sendMessageToConversation(convoId, message, accessToken)
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage
            }
        }
    }
}
