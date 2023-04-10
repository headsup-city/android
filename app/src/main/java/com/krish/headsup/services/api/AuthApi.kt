package com.krish.headsup.services.api

import com.krish.headsup.model.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApi {
    @POST("/v1/auth/register")
    suspend fun register(@Body body: RegisterRequestBody): Response<UserRegisterResponse>

    @POST("/v1/auth/login")
    suspend fun login(@Body body: LoginRequestBody): Response<UserLoginResponse>

    @POST("/v1/auth/signin-with-google")
    suspend fun signinWithGoogle(@Body body: GoogleSignInRequestBody): Response<UserLoginResponse>

    @POST("/v1/auth/signin-with-apple")
    suspend fun signinWithApple(@Body body: AppleSignInRequestBody): Response<UserLoginResponse>

    @POST("/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequestBody): ResponseBody

    @POST("/v1/auth/reset-password")
    suspend fun resetPassword(
        @Query("token") token: String,
        @Body body: ResetPasswordRequestBody
    ): ResponseBody

    @POST("/v1/auth/verify-email")
    suspend fun verifyEmail(@Query("token") token: String): ResponseBody

    @POST("/v1/auth/send-verification-email")
    suspend fun sendVerificationEmail(): ResponseBody

    @POST("/v1/auth/refresh-tokens")
    suspend fun refreshTokens(@Body body: RefreshTokensRequestBody): Response<TokenStore>

    @POST("/v1/auth/logout")
    suspend fun logout(@Body body: LogoutRequestBody): ResponseBody
}
