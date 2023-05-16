package com.krish.headsup.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.krish.headsup.model.Conversation
import com.krish.headsup.model.ConversationTypes
import com.krish.headsup.model.Message
import com.krish.headsup.model.MessageType
import com.krish.headsup.model.User
import com.krish.headsup.repositories.ConversationRepository
import com.krish.headsup.repositories.MessageRepository
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.ConversationResult
import com.krish.headsup.utils.MessagesResult
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.SendMessageToConversationResult
import com.krish.headsup.utils.SendMessageToUserResult
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MessagingViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val convoRepository: ConversationRepository,
    private val messageRepository: MessageRepository,
    private val tokenManager: TokenManager,
) : ViewModel() {

    var currentMessageResult: Flow<PagingData<Message>>? = null

    private val _convoData = MutableLiveData<Conversation>()
    val convoData: LiveData<Conversation> get() = _convoData

    private val _otherUser = MutableLiveData<User>()
    val otherUser: LiveData<User> get() = _otherUser

    private val _conversationType = MutableLiveData<ConversationTypes>()
    val conversationType: LiveData<ConversationTypes> get() = _conversationType

    private val _loadingStatus = MutableLiveData<LoadingStatus>()
    val loadingStatus: LiveData<LoadingStatus> get() = _loadingStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    private val _latestMessage = MutableLiveData<Message>()
    val latestMessage: LiveData<Message> get() = _latestMessage

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    private var currentUser: User? = null

    fun updateUser(user: User) {
        currentUser = user
    }

    fun initialize(userId: String?, convoId: String?) {
        viewModelScope.launch {
            if (convoId != null) {
                // If we have a conversation ID, get the conversation
                getConversationById(convoId)
            } else if (userId != null) {
                // If we only have a user ID, try to get the conversation with that user
                getConversationByUserId(userId)
            }
            // If neither ID is provided or if getting the conversation fails, don't do anything
        }
    }

    private suspend fun getConversationById(convoId: String) {
        val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
        val result = convoRepository.getConvoById(accessToken, convoId)
        handleConversationResult(result)
    }

    private suspend fun getConversationByUserId(userId: String) {
        val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
        val result = convoRepository.getConvoByUserId(accessToken, userId)
        handleConversationResult(result)
    }

    private fun handleConversationResult(result: Result) {
        when (result) {
            is ConversationResult -> {
                _convoData.value = result.data

                // After successfully getting the conversation, fetch the initial messages
                getInitialMessages(result.data.id)

                // The other user is the one in the participants list that is not the current user
                val otherUserId = result.data.participants?.first { it != currentUser?.id }

                if (!otherUserId.isNullOrEmpty()) {
                    getOtherUser(otherUserId)
                }
            }
            is Result.Error -> {
                // Log or display the error message, but don't do anything else
                _errorMessage.value = result.exception.message
            }
            else -> {
                // Handle other cases or do nothing
            }
        }
    }

    private fun getInitialMessages(convoId: String) {
        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
            val result = messageRepository.getInitialMessagesByConvoId(convoId, accessToken)
            when (result) {
                is MessagesResult -> {
                    _messages.value = result.data.results
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                else -> {
                    // Handle other cases or do nothing
                }
            }
        }
    }

    fun sendMessageToUser(userId: String, message: String) {
        val tempMessage = Message(
            id = "temp", // Temporary ID
            text = message,
            messageType = MessageType.TEXT, // Assume it's a text message
            attachment = null, // No attachment
            author = currentUser!!, // This is a guess
            conversation = "", // Assume the conversation ID is the user ID
            createdAt = Instant.now().toString(), // Current timestamp
            updatedAt = Instant.now().toString() // Current timestamp
        )

        // Optimistically add the temporary message to the chat UI
        _latestMessage.value = tempMessage
        appendMessage(tempMessage)

        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
            val result = messageRepository.sendMessageToUser(userId, message, accessToken)
            when (result) {
                is SendMessageToUserResult -> {
                    // Handle success case
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                else -> {
                    // Handle other cases or do nothing
                }
            }
        }
    }

    fun sendMessageToConversation(convoId: String, message: String) {
        val tempMessage = Message(
            id = "temp", // Temporary ID
            text = message,
            messageType = MessageType.TEXT, // Assume it's a text message
            attachment = null, // No attachment
            author = currentUser!!, // This is a guess
            conversation = convoId, // Use the conversation ID
            createdAt = Instant.now().toString(), // Current timestamp
            updatedAt = Instant.now().toString() // Current timestamp
        )

        // Optimistically add the temporary message to the chat UI
        _latestMessage.value = tempMessage
        appendMessage(tempMessage)

        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
            val result = messageRepository.sendMessageToConversation(convoId, message, accessToken)
            when (result) {
                is SendMessageToConversationResult -> {
                    // Handle success case
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message
                }
                else -> {
                    // Handle other cases or do nothing
                }
            }
        }
    }

    private fun getOtherUser(otherUserId: String) {
        // Get the user with this ID
        viewModelScope.launch {
            val accessToken = tokenManager.getTokenStore()?.access?.token ?: ""
            val result = userRepository.getUser(accessToken, otherUserId)
            when (result) {
                is UserResult -> {
                    // Successfully got the other user, update the LiveData
                    _otherUser.value = result.data
                }
                is Result.Error -> {
                    // Handle the error
                    _errorMessage.value = result.exception.message
                }
                else -> {
                    // Handle other cases or do nothing
                }
            }
        }
    }

    private fun appendMessage(message: Message) {
        val currentMessages = _messages.value.orEmpty().toMutableList()
        currentMessages.add(0, message) // Add at start of list
        _messages.value = currentMessages
    }

    enum class LoadingStatus {
        Loading,
        Success,
        Error,
    }
}
