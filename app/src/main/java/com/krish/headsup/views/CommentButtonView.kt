package com.krish.headsup.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.krish.headsup.R

class CommentButtonView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val commentIcon: ImageView
    private val commentCount: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.comment_button_view, this, true)
        commentIcon = findViewById(R.id.commentButton)
        commentCount = findViewById(R.id.commentCount)
    }

    fun setCommentCount(count: Int) {
        commentCount.text = count.toString()
    }
}
