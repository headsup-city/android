package com.krish.headsup.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.krish.headsup.R
import com.krish.headsup.model.Post

class PostView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private lateinit var postType: String
    private val contentContainer: FrameLayout
    private val likeButton: LikeButtonView
    private val commentButton: CommentButtonView

    init {
        LayoutInflater.from(context).inflate(R.layout.post_view, this, true)
        contentContainer = findViewById(R.id.postContentContainer)
        likeButton = findViewById(R.id.likeButton)
        commentButton = findViewById(R.id.commentButton)
    }


    fun setPost(post: Post) {
        postType = post.postType ?: "text"
        when (postType) {
            "text" -> {
                val textView = TextView(context)
                textView.text = post.caption
                contentContainer.addView(textView)
            }
            "image" -> {
                val imageView = ImageView(context)
                imageView.adjustViewBounds = true
                Glide.with(context).load(post.imageUri).into(imageView)
                contentContainer.addView(imageView)
            }
            "video" -> {
                // Handle video post type here
            }
        }

        likeButton.setLikeCount(post.likeCount ?: 0)
        commentButton.setCommentCount(post.commentCount ?: 0)
    }
}