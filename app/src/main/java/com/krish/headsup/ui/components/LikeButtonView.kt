package com.krish.headsup.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.krish.headsup.R

class LikeButtonView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val likeCount: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.like_button_view, this, true)
        likeCount = findViewById(R.id.likeCount)
    }

    fun setLikeCount(count: Int) {
        likeCount.text = count.toString()
    }
}
