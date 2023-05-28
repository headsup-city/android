package com.krish.headsup.utils

import android.graphics.Rect
import android.view.TouchDelegate
import android.view.View

class ViewUtils {
    fun increaseTouchableArea(view: View, extraPaddingRes: Int) {
        (view.parent as View).post {
            val rect = Rect()
            view.getHitRect(rect)

            val extraPadding = view.context.resources.getDimensionPixelSize(extraPaddingRes)
            rect.top -= extraPadding
            rect.left -= extraPadding
            rect.right += extraPadding
            rect.bottom += extraPadding

            (view.parent as View).touchDelegate = TouchDelegate(rect, view)
        }
    }
}
