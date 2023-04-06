package com.krish.headsup.utils

import com.krish.headsup.model.AuthState

interface AuthStateChangeListener {
    fun onAuthStateChanged(authState: AuthState)
}
