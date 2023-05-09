package com.krish.headsup.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.coordinatorlayout.widget.CoordinatorLayout

class CustomCoordinatorLayout(context: Context, attrs: AttributeSet) : CoordinatorLayout(context, attrs) {

    private var clickListener: OnClickListener? = null

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            clickListener?.onClick(this)
        }
        return super.onTouchEvent(event)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = l
        super.setOnClickListener(l)
    }
}
