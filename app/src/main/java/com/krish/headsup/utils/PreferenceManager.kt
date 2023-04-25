package com.krish.headsup.utils

import android.content.Context

object PreferenceManager {
    private const val PREFS_NAME = "app_preferences"
    private const val MUTE_STATE_KEY = "mute_state"

    fun setMuteState(context: Context, state: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(MUTE_STATE_KEY, state).apply()
    }

    fun getMuteState(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(MUTE_STATE_KEY, false)
    }
}
