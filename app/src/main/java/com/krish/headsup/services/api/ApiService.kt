package com.krish.headsup.services.api

import com.krish.headsup.managers.AuthManager
import com.krish.headsup.utils.AuthInterceptor
import com.krish.headsup.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object ApiService {
    private lateinit var retrofit: Retrofit
    private lateinit var tokenManager: TokenManager

    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)

    val commentApi: CommentApi
        get() = retrofit.create(CommentApi::class.java)

    val conversationApi: ConversationApi
        get() = retrofit.create(ConversationApi::class.java)

    val followApi: FollowApi
        get() = retrofit.create(FollowApi::class.java)

    val mediaApi: MediaApi
        get() = retrofit.create(MediaApi::class.java)

    val messageApi: MessageApi
        get() = retrofit.create(MessageApi::class.java)

    val postApi: PostApi
        get() = retrofit.create(PostApi::class.java)

    val reportApi: ReportApi
        get() = retrofit.create(ReportApi::class.java)

    val userApi: UserApi
        get() = retrofit.create(UserApi::class.java)

    fun init(retrofit: Retrofit, tokenManager: TokenManager, authManager: AuthManager) {
        this.tokenManager = tokenManager

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenManager, authManager)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        this.retrofit = retrofit.newBuilder()
            .client(okHttpClient)
            .build()
    }
}
