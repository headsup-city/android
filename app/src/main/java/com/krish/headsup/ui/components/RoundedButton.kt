package com.krish.headsup.ui.components

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import com.krish.headsup.R

class RoundedButton : AppCompatButton {

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        attrs?.let {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedButton)
            val buttonColor: Int = a.getColor(R.styleable.RoundedButton_buttonColor, context.resources.getColor(R.color.purple_200))
            val cornerRadius: Int = a.getDimensionPixelSize(R.styleable.RoundedButton_cornerRadius, 12)
            val icon: Drawable? = a.getDrawable(R.styleable.RoundedButton_icon)
            a.recycle()

            val backgroundDrawable = GradientDrawable()
            backgroundDrawable.setColor(buttonColor)
            backgroundDrawable.cornerRadius = cornerRadius.toFloat()

            background = backgroundDrawable

            // Set the icon to the left of the text
            icon?.let {
                val drawablePadding = 8 // You can adjust this value as needed
                setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
                compoundDrawablePadding = drawablePadding
            }
        }
    }
}
