package com.krish.headsup.di

import android.content.Context
import com.krish.headsup.BuildConfig
import com.krish.headsup.services.api.ApiService
import com.krish.headsup.services.api.AuthApi
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private fun getBaseUrl(context: Context): String {
        val configJson = context.assets.open("config.json").bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(configJson)
        val environment = if (BuildConfig.DEBUG) "dev" else "prod"
        return jsonObject.getJSONObject(environment).getString("baseUrl")
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(getBaseUrl(context))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        ApiService.init(retrofit)
        return ApiService.authApi
    }

    @Provides
    @Singleton
    fun provideUserPreferences(context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(context: Context): TokenManager {
        return TokenManager(context)
    }
}
