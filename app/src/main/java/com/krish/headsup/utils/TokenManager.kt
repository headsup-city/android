package com.krish.headsup.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.krish.headsup.model.TokenStore

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveTokens(tokenStore: TokenStore) {
        val jsonTokenStore = gson.toJson(tokenStore)
        with(sharedPreferences.edit()) {
            putString("tokenStore", jsonTokenStore)
            apply()
        }
    }

    fun getTokenStore(): TokenStore? {
        val jsonTokenStore = sharedPreferences.getString("tokenStore", null)
        return if (jsonTokenStore != null) gson.fromJson(jsonTokenStore, TokenStore::class.java) else null
    }

    fun clearTokens() {
        with(sharedPreferences.edit()) {
            remove("tokenStore")
            apply()
        }
    }
}
