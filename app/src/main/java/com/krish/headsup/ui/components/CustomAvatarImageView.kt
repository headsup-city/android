package com.krish.headsup.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.krish.headsup.R

class CustomAvatarImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var defaultAvatarPaddingTop = 0

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomAvatarImageView)
        defaultAvatarPaddingTop = typedArray.getDimensionPixelSize(R.styleable.CustomAvatarImageView_defaultAvatarPaddingTop, 0)
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable != null) {
            val saveCount = canvas.save()
            canvas.translate(0f, if (drawable == defaultAvatar) defaultAvatarPaddingTop.toFloat() else 0f)
            super.onDraw(canvas)
            canvas.restoreToCount(saveCount)
        } else {
            super.onDraw(canvas)
        }
    }

    companion object {
        var defaultAvatar: Drawable? = null
    }
}
