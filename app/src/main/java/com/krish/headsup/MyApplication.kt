package com.krish.headsup

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.Configuration
import com.krish.headsup.services.api.ApiService
import com.krish.headsup.utils.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var hiltWorkerFactory: HiltWorkerFactory
    lateinit var database: AppDatabase

    override fun onCreate() {
        super.onCreate()
        ApiService.init(this)

        // Initialize Room database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(hiltWorkerFactory)
            .build()
    }
}
