package com.krish.headsup.services.api

import retrofit2.Retrofit

object ApiService {
    private lateinit var retrofit: Retrofit

    val authApi: AuthApi
        get() = retrofit.create(AuthApi::class.java)

    fun init(retrofit: Retrofit) {
        this.retrofit = retrofit
    }
}
