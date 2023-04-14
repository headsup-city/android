import com.krish.headsup.model.TokenStore
import com.krish.headsup.utils.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.coroutines.suspendCoroutine

class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val tokenStore = tokenManager.getTokenStore()
        val request = chain.request()

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

    private suspend fun refreshToken(tokenManager: TokenManager): TokenStore? = suspendCoroutine { continuation ->
        // Implement the function to refresh the tokens using the refresh token and return the new tokens
        // When you have the result, use `continuation.resume(yourResult)` to return the value
    }
}
