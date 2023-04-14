package com.krish.headsup

import android.app.Application
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.utils.AppDatabase
import com.krish.headsup.utils.TokenManager
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var database: AppDatabase
    @Inject
    lateinit var retrofit: Retrofit
    @Inject
    lateinit var authManager: AuthManager
    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate() {
        super.onCreate()
    }
}
