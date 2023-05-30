package com.krish.headsup.utils

import com.krish.headsup.model.Conversation
import com.krish.headsup.model.GetMessagesByConversationIdResponse
import com.krish.headsup.model.Message
import com.krish.headsup.model.SendMessageToConversationResponse
import com.krish.headsup.model.SendMessageToUserResponse
import com.krish.headsup.model.UpdateAvatarResponse
import com.krish.headsup.model.User
import com.krish.headsup.model.UserSearchResponse

sealed class Result {
    data class Error(val exception: Throwable) : Result()
}

data class ConversationResult(val data: Conversation) : Result()

data class UserResult(val data: User) : Result()

data class MessageResult(val data: Message) : Result()

data class UnitResult(val data: Unit) : Result()

data class SendMessageToUserResult(val data: SendMessageToUserResponse) : Result()

data class SendMessageToConversationResult(val data: SendMessageToConversationResponse) : Result()

data class MessagesResult(val data: GetMessagesByConversationIdResponse) : Result()

data class AvatarResult(val data: UpdateAvatarResponse) : Result()

data class UserSearchResult(val data: UserSearchResponse) : Result()
