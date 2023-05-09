package com.krish.headsup.utils

import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.model.RefreshTokensRequest
import com.krish.headsup.model.TokenStore
import com.krish.headsup.services.api.AuthApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Provider

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val authManager: AuthManager,
    private val authApiProvider: Provider<AuthApi>
) : Interceptor {
    // Add the excluded API patterns here
    private val excludedApiPatterns = listOf(
        Regex("/v1/auth/.*"),
    )
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestPath = request.url.encodedPath

        // Check if the request path matches any of the excluded patterns
        if (excludedApiPatterns.any { pattern -> pattern.matches(requestPath) }) {
            return chain.proceed(request)
        }

        val tokenStore = tokenManager.getTokenStore()

        if (tokenStore != null) {
            val accessToken = tokenStore.access.token

            // Check if the access token is about to expire or has expired
            if (isTokenExpired(tokenStore.access.expires)) {
                // Refresh the tokens
                val refreshedTokens = runBlocking { refreshToken(tokenManager) } // Use runBlocking to call refreshToken
                if (refreshedTokens != null) {
                    tokenManager.saveTokens(refreshedTokens)
                    return proceedWithNewToken(chain, request, refreshedTokens.access.token)
                }
            } else {
                return proceedWithNewToken(chain, request, accessToken)
            }
        } else {
            authManager.updateAuthState(AuthState.NO_USER)
        }
        return chain.proceed(request)
    }

    private fun proceedWithNewToken(chain: Interceptor.Chain, request: okhttp3.Request, accessToken: String): Response {
        val authenticatedRequest = request.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()
        return chain.proceed(authenticatedRequest)
    }

    private fun isTokenExpired(expires: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        val expirationDate = dateFormat.parse(expires) ?: return false

        val currentTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        val tenSecondsBeforeExpiration = expirationDate.time - 10000 // Subtract 10 seconds (10000 milliseconds)

        return currentTime.time >= tenSecondsBeforeExpiration
    }

    private suspend fun refreshToken(tokenManager: TokenManager): TokenStore? {
        val refreshToken = tokenManager.getTokenStore()?.refresh?.token ?: return null

        return try {
            val response = withContext(Dispatchers.IO) {
                authApiProvider.get().refreshTokens(RefreshTokensRequest(refreshToken))
            }

            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
