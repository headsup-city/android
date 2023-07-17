package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("signUpSource")
    val signUpSource: SignUpSource
)
data class RegistrationResponse(
    @SerializedName("tokens")
    val tokens: TokenStore,

    @SerializedName("user")
    val user: User
)

data class LoginRequest(
    @SerializedName("loginId")
    val loginId: String,

    @SerializedName("password")
    val password: String
)
data class LoginResponse(
    @SerializedName("tokens")
    val tokens: TokenStore,

    @SerializedName("user")
    val user: User
)

data class GoogleSignInRequest(
    @SerializedName("accessToken")
    val idToken: String,

    @SerializedName("signUpSource")
    val signUpSource: SignUpSource
)

data class GoogleSignInIdTokenRequest(
    @SerializedName("idToken")
    val idToken: String,

    @SerializedName("signUpSource")
    val signUpSource: SignUpSource
)

data class AppleSignInRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("identityToken")
    val identityToken: String,

    @SerializedName("appleUserId")
    val appleUserId: String,

    @SerializedName("signUpSource")
    val signUpSource: SignUpSource
)

data class PostCommentsResponse(
    @SerializedName("limit")
    val limit: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Comment>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalResults")
    val totalResults: Int
)

data class SendMessageToUserRequest(
    @SerializedName("text")
    val text: String,

    @SerializedName("messageType")
    val messageType: String = "TEXT"
)

data class SendMessageToUserResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("messageType")
    val messageType: String = "TEXT",

    @SerializedName("text")
    val text: String,

    @SerializedName("attachment")
    val attachment: String?,

    @SerializedName("author")
    val author: User,

    @SerializedName("conversation")
    val conversation: Conversation,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class SendMessageToConversationResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("messageType")
    val messageType: String,

    @SerializedName("text")
    val text: String,

    @SerializedName("attachment")
    val attachment: String?,

    @SerializedName("author")
    val author: User,

    @SerializedName("conversation")
    val conversation: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String
)

data class GetMessagesByConversationIdResponse(
    @SerializedName("limit")
    val limit: Int = 20,

    @SerializedName("page")
    val page: Int = 0,

    @SerializedName("results")
    val results: List<Message>,

    @SerializedName("totalPages")
    val totalPages: Int = 1,

    @SerializedName("totalResults")
    val totalResults: Int = 2
)

data class SendMessageToConversationRequest(
    @SerializedName("text")
    val text: String,

    @SerializedName("messageType")
    val messageType: String = "TEXT"
)

data class PostListResponse(
    @SerializedName("limit")
    val limit: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<Post>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalResults")
    val totalResults: Int
)

data class UserSearchResponse(
    @SerializedName("limit")
    val limit: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<User>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalResults")
    val totalResults: Int
)

data class UpdatePasswordRequest(
    @SerializedName("oldPassword")
    val oldPassword: String,

    @SerializedName("newPassword")
    val newPassword: String
)

data class UpdateUserRequest(
    @SerializedName("name")
    val name: String?,

    @SerializedName("mobileNumber")
    val mobileNumber: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    val password: String?
)

data class ConversationListResponse(
    @SerializedName("limit")
    val limit: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("results")
    val results: List<ConversationFull>,

    @SerializedName("totalPages")
    val totalPages: Int,

    @SerializedName("totalResults")
    val totalResults: Int
)

data class PushTokenSubscriptionRequest(
    @SerializedName("pushToken")
    val pushToken: String
)

data class UpdateAvatarResponse(
    @SerializedName("data")
    val data: AvatarData
)

data class AvatarData(
    @SerializedName("avatar")
    val avatar: String,

    @SerializedName("avatarUri")
    val avatarUri: String
)

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class ResetPasswordRequest(
    @SerializedName("password")
    val password: String
)

data class RefreshTokensRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class LogoutRequest(
    @SerializedName("refreshToken")
    val refreshToken: String
)
