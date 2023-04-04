package com.krish.headsup

import android.app.Application
import com.krish.headsup.services.api.ApiService

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ApiService.init(this)
    }
}
