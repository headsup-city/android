package com.krish.headsup.services.api

import android.content.Context
import com.krish.headsup.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private fun getBaseUrl(context: Context): String {
    val configJson = context.assets.open("config.json").bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(configJson)
    val environment = if (BuildConfig.DEBUG) "dev" else "prod"
    return jsonObject.getJSONObject(environment).getString("baseUrl")
}

object ApiService {
    private lateinit var baseUrl: String

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(
            HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        )
        .build()

    private lateinit var retrofit: Retrofit

    val userApi: UserApi
        get() = retrofit.create(UserApi::class.java)

    val postApi: PostApi
        get() = retrofit.create(PostApi::class.java)

    fun init(context: Context) {
        baseUrl = getBaseUrl(context)
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
    }
}
