package com.krish.headsup.di

import android.content.Context
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import com.krish.headsup.BuildConfig
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.repositories.PostRepository
import com.krish.headsup.services.api.ApiService
import com.krish.headsup.services.api.AuthApi
import com.krish.headsup.services.api.CommentApi
import com.krish.headsup.services.api.ConversationApi
import com.krish.headsup.services.api.FollowApi
import com.krish.headsup.services.api.MediaApi
import com.krish.headsup.services.api.MessageApi
import com.krish.headsup.services.api.PostApi
import com.krish.headsup.services.api.ReportApi
import com.krish.headsup.services.api.UserApi
import com.krish.headsup.utils.AuthInterceptor
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
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
    fun provideAuthManager(): AuthManager {
        return AuthManager()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager, authManager: AuthManager): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val authInterceptor = AuthInterceptor(tokenManager, authManager)

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, context: Context, tokenManager: TokenManager, authManager: AuthManager): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl(context))
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        ApiService.init(retrofit, tokenManager, authManager)

        return retrofit
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return ApiService.authApi
    }

    @Provides
    @Singleton
    fun provideCommentApi(retrofit: Retrofit): CommentApi {
        return ApiService.commentApi
    }

    @Provides
    @Singleton
    fun provideConversationApi(retrofit: Retrofit): ConversationApi {
        return ApiService.conversationApi
    }

    @Provides
    @Singleton
    fun provideFollowApi(retrofit: Retrofit): FollowApi {
        return ApiService.followApi
    }

    @Provides
    @Singleton
    fun provideMediaApi(retrofit: Retrofit): MediaApi {
        return ApiService.mediaApi
    }

    @Provides
    @Singleton
    fun provideMessageApi(retrofit: Retrofit): MessageApi {
        return ApiService.messageApi
    }

    @Provides
    @Singleton
    fun providePostApi(retrofit: Retrofit): PostApi {
        return ApiService.postApi
    }

    @Provides
    @Singleton
    fun provideReportApi(retrofit: Retrofit): ReportApi {
        return ApiService.reportApi
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return ApiService.userApi
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

    @Provides
    @Singleton
    fun providePostRepository(postApi: PostApi): PostRepository {
        return PostRepository(postApi)
    }
}
