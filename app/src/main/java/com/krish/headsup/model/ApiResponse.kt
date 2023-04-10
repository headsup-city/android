package com.krish.headsup.model

// UserRegisterResponse.kt
data class UserRegisterResponse(
    val tokens: TokenStore,
    val user: User
)

// UserLoginResponse.kt
data class UserLoginResponse(
    val tokens: TokenStore,
    val user: User
)

data class RegisterRequestBody(
    val name: String,
    val email: String,
    val password: String,
    val signUpSource: Map<String, String>
)

data class LoginRequestBody(
    val loginId: String,
    val password: String
)

data class GoogleSignInRequestBody(
    val accessToken: String,
    val signUpSource: Map<String, String>
)

data class AppleSignInRequestBody(
    val name: String,
    val email: String,
    val identityToken: String,
    val appleUserId: String,
    val signUpSource: Map<String, String>
)

data class ForgotPasswordRequestBody(val email: String)
data class ResetPasswordRequestBody(val password: String)
data class RefreshTokensRequestBody(val refreshToken: String)
data class LogoutRequestBody(val refreshToken: String)
