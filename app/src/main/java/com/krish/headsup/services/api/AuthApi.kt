package com.krish.headsup.services.api

import com.krish.headsup.model.AppleSignInRequest
import com.krish.headsup.model.ForgotPasswordRequest
import com.krish.headsup.model.GoogleSignInRequest
import com.krish.headsup.model.LoginRequest
import com.krish.headsup.model.LoginResponse
import com.krish.headsup.model.LogoutRequest
import com.krish.headsup.model.RefreshTokensRequest
import com.krish.headsup.model.RegistrationRequest
import com.krish.headsup.model.RegistrationResponse
import com.krish.headsup.model.ResetPasswordRequest
import com.krish.headsup.model.TokenStore
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("/v1/auth/register")
    suspend fun register(@Body body: RegistrationRequest): Response<RegistrationResponse>

    @POST("/v1/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>

    @POST("/v1/auth/signin-with-google")
    suspend fun signinWithGoogle(@Body body: GoogleSignInRequest): Response<LoginResponse>

    @POST("/v1/auth/signin-with-apple")
    suspend fun signinWithApple(@Body body: AppleSignInRequest): Response<LoginResponse>

    @POST("/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): ResponseBody

    @POST("/v1/auth/reset-password")
    suspend fun resetPassword(
        @Query("token") token: String,
        @Body body: ResetPasswordRequest
    ): ResponseBody

    @POST("/v1/auth/verify-email")
    suspend fun verifyEmail(@Query("token") token: String): ResponseBody

    @POST("/v1/auth/send-verification-email")
    suspend fun sendVerificationEmail(): ResponseBody

    @POST("/v1/auth/refresh-tokens")
    suspend fun refreshTokens(@Body body: RefreshTokensRequest): Response<TokenStore>

    @POST("/v1/auth/logout")
    suspend fun logout(@Body body: LogoutRequest): ResponseBody
}
