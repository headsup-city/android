package com.krish.headsup

import android.app.Application
import com.krish.headsup.services.api.ApiService
import com.krish.headsup.utils.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import retrofit2.Retrofit
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var database: AppDatabase

    @Inject
    lateinit var retrofit: Retrofit

    override fun onCreate() {
        super.onCreate()
        ApiService.init(retrofit)
    }
}
