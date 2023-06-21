package com.krish.headsup.repositories

import com.krish.headsup.model.AppleSignInRequest
import com.krish.headsup.model.ForgotPasswordRequest
import com.krish.headsup.model.GoogleSignInRequest
import com.krish.headsup.model.LoginRequest
import com.krish.headsup.model.LogoutRequest
import com.krish.headsup.model.RefreshTokensRequest
import com.krish.headsup.model.RegistrationRequest
import com.krish.headsup.model.ResetPasswordRequest
import com.krish.headsup.services.api.AuthApi
import com.krish.headsup.utils.LoginResult
import com.krish.headsup.utils.RegistrationResult
import com.krish.headsup.utils.Result
import com.krish.headsup.utils.UnitResult
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authApi: AuthApi) {

    suspend fun register(body: RegistrationRequest): Result {
        return try {
            val response = authApi.register(body)
            if (response.isSuccessful && response.body() != null) {
                RegistrationResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to register"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun login(body: LoginRequest): Result {
        return try {
            val response = authApi.login(body)
            if (response.isSuccessful && response.body() != null) {
                LoginResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to login"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signinWithGoogle(body: GoogleSignInRequest): Result {
        return try {
            val response = authApi.signinWithGoogle(body)
            if (response.isSuccessful && response.body() != null) {
                LoginResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to sign in with Google"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signinWithApple(body: AppleSignInRequest): Result {
        return try {
            val response = authApi.signinWithApple(body)
            if (response.isSuccessful && response.body() != null) {
                LoginResult(response.body()!!)
            } else {
                Result.Error(Exception("Failed to sign in with Apple"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun forgotPassword(body: ForgotPasswordRequest): Result {
        return try {
            val response = authApi.forgotPassword(body)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to request password reset"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun resetPassword(token: String, body: ResetPasswordRequest): Result {
        return try {
            val response = authApi.resetPassword(token, body)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to reset password"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun verifyEmail(token: String): Result {
        return try {
            val response = authApi.verifyEmail(token)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to verify email"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun sendVerificationEmail(): Result {
        return try {
            val response = authApi.sendVerificationEmail()
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to send verification email"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun refreshTokens(body: RefreshTokensRequest): Result {
        return try {
            val response = authApi.refreshTokens(body)
            if (response.isSuccessful && response.body() != null) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to refresh tokens"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun logout(body: LogoutRequest): Result {
        return try {
            val response = authApi.logout(body)
            if (response.isSuccessful) {
                UnitResult(Unit)
            } else {
                Result.Error(Exception("Failed to logout"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
