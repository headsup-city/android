package com.krish.headsup.model

import com.google.gson.annotations.SerializedName

data class UserRegisterResponse(
    @SerializedName("tokens")
    val tokens: TokenStore,

    @SerializedName("user")
    val user: User
)

data class UserLoginResponse(
    @SerializedName("tokens")
    val tokens: TokenStore,

    @SerializedName("user")
    val user: User
)

data class RegisterRequestBody(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("signUpSource")
    val signUpSource: Map<String, String>
)

data class LoginRequestBody(
    @SerializedName("loginId")
    val loginId: String,

    @SerializedName("password")
    val password: String
)

data class GoogleSignInRequestBody(
    @SerializedName("accessToken")
    val accessToken: String,

    @SerializedName("signUpSource")
    val signUpSource: Map<String, String>
)

data class AppleSignInRequestBody(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("identityToken")
    val identityToken: String,

    @SerializedName("appleUserId")
    val appleUserId: String,

    @SerializedName("signUpSource")
    val signUpSource: Map<String, String>
)

data class CommentForAPostResType(
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

data class SendMessageToUserBodyType(
    @SerializedName("text")
    val text: String,

    @SerializedName("messageType")
    val messageType: String = "TEXT"
)

data class SendMessageToUserResType(
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

data class SendMessageToConvoResType(
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

data class GetMessageByConvoIdResType(
    @SerializedName("limit")
    val limit: Int = 20,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("results")
    val results: List<Message>,

    @SerializedName("totalPages")
    val totalPages: Int = 1,

    @SerializedName("totalResults")
    val totalResults: Int = 2
)

data class SendMessageToConvoBodyType(
    @SerializedName("text")
    val text: String,

    @SerializedName("messageType")
    val messageType: String = "TEXT"
)

data class GeneralPostResType(
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

data class UserSearchResType(
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

data class UserUpdatePassReqBodyType(
    @SerializedName("oldPassword")
    val oldPassword: String,

    @SerializedName("newPassword")
    val newPassword: String
)

data class UserUpdateReqBodyType(
    @SerializedName("name")
    val name: String?,

    @SerializedName("mobileNumber")
    val mobileNumber: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("password")
    val password: String?
)

data class GetConversationsApiResType(
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


data class SubscribePushTokenReqBodyType(
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

data class ForgotPasswordRequestBody(
    @SerializedName("email")
    val email: String
)

data class ResetPasswordRequestBody(
    @SerializedName("password")
    val password: String
)

data class RefreshTokensRequestBody(
    @SerializedName("refreshToken")
    val refreshToken: String
)

data class LogoutRequestBody(
    @SerializedName("refreshToken")
    val refreshToken: String
)
