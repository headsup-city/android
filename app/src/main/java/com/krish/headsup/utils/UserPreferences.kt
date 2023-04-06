package com.krish.headsup.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.krish.headsup.model.User

class UserPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor
    private val gson = Gson()

    init {
        sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveUser(user: User) {
        val jsonUser = gson.toJson(user)
        editor.putString("user", jsonUser)
        editor.apply()
    }

    fun getUser(): User? {
        val jsonUser = sharedPreferences.getString("user", null)
        return if (jsonUser != null) gson.fromJson(jsonUser, User::class.java) else null
    }

    fun deleteUser() {
        editor.remove("user")
        editor.apply()
    }
}
