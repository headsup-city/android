package com.krish.headsup.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    fun saveTokens(accessToken: String, refreshToken: String) {
        with(sharedPreferences.edit()) {
            putString("accessToken", accessToken)
            putString("refreshToken", refreshToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString("accessToken", null)
    fun getRefreshToken(): String? = sharedPreferences.getString("refreshToken", null)
    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove("accessToken")
            remove("refreshToken")
            apply()
        }
    }
}
