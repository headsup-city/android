package com.krish.headsup.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.krish.headsup.R

class CommentButtonView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val commentCount: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.comment_button_view, this, true)
        commentCount = findViewById(R.id.commentCount)
    }

    fun setCommentCount(count: Int) {
        commentCount.text = count.toString()
    }
}
