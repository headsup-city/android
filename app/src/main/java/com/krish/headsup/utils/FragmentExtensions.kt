package com.krish.headsup.utils

import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment

fun Fragment.makeStatusBarTranslucent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        requireActivity().window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    } else {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }
}

fun Fragment.restoreStatusBar() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        requireActivity().window.apply {
            statusBarColor = Color.BLACK // Replace with your desired status bar color
        }
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    } else {
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}
