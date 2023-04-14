package com.krish.headsup.services.api

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

    fun init(retrofit: Retrofit, tokenManager: TokenManager) {
        this.tokenManager = tokenManager

        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenManager)

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
