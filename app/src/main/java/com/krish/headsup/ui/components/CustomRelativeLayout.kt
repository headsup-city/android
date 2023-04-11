package com.krish.headsup.ui.components

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

class CustomRelativeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}
