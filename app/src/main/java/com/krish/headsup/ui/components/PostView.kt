package com.krish.headsup.ui.components

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.model.Post
import com.krish.headsup.utils.dpToPx
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class PostView(itemView: View, private val screenWidth: Int, private val onCommentClickListener:OnCommentClickListener,private val onAuthorClickListener: OnAuthorClickListener) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context

    private val authorAvatar: CustomAvatarImageView = itemView.findViewById(R.id.authorAvatar)
    private val authorName: TextView = itemView.findViewById(R.id.authorName)
    private val postDate: TextView = itemView.findViewById(R.id.postDate)
    private val postImage: ImageView = itemView.findViewById(R.id.postImage)
    private val postText: TextView = itemView.findViewById(R.id.postText)
    private val customVideoPlayer: CustomVideoPlayer = itemView.findViewById(R.id.customVideoPlayer)
    private val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    private val likeCountText: TextView = itemView.findViewById(R.id.likeCount)
    private val commentButton: ImageView = itemView.findViewById(R.id.commentButton)
    private val commentCountText: TextView = itemView.findViewById(R.id.commentCount)
    private val shareButton: ImageView = itemView.findViewById(R.id.shareButton)

    fun bind(post: Post?) {
        // Reset all views to their initial state
        likeCountText.visibility = View.GONE
        commentCountText.visibility = View.GONE
        postImage.visibility = View.GONE
        postText.visibility = View.GONE
        customVideoPlayer.visibility = View.GONE

        post?.let {
            // Load the author's avatar using Glide
            if (CustomAvatarImageView.defaultAvatar == null) {
                CustomAvatarImageView.defaultAvatar = ContextCompat.getDrawable(context, R.drawable.default_avatar)
            }

            GlideApp.with(context)
                .load(it.author?.avatarUri)
                .signature(CustomCacheKeyGenerator(it.author?.avatarUri ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(authorAvatar)

            authorName.text = it.author?.name ?: context.getString(R.string.unknown_author)
            postDate.text = getRelativeTime(it.createdAt, context)
            postText.text = it.caption

            if (it.caption.isNullOrEmpty()) {
                postText.visibility = View.GONE
            } else { postText.visibility = View.VISIBLE }

            when (it.postType) {
                "PRIMARY" -> {
                    it.attachment?.uri?.let { uri ->
                        postImage.visibility = View.VISIBLE

                        // Calculate the height based on the aspect ratio
                        val aspectRatio = it.attachment?.height?.toFloat()?.div(it.attachment?.width ?: 1) ?: 0f
                        val calculatedHeight = (screenWidth * aspectRatio).toInt()

                        // Set the maxHeight constraint
                        val maxHeightInPx = dpToPx(context, 350f)
                        val finalHeight = if (calculatedHeight > maxHeightInPx) maxHeightInPx else calculatedHeight

                        // Update the ImageView's height
                        val layoutParams = postImage.layoutParams
                        layoutParams.height = finalHeight
                        postImage.layoutParams = layoutParams

                        // Load the image using Glide with CustomCacheKeyGenerator
                        GlideApp.with(context)
                            .load(uri)
                            .signature(CustomCacheKeyGenerator(uri))
                            .centerCrop()
                            .into(postImage)
                    }
                }

                "SHORT" -> {
                    customVideoPlayer.visibility = View.VISIBLE
                    it.attachment?.uri?.let { uri -> customVideoPlayer.setVideoUri(uri) }
                }
            }

            if (it.likeCount!! > 0) {
                likeCountText.visibility = View.VISIBLE
                likeCountText.text = it.likeCount.toString()
            }

            if (it.commentCount!! > 0) {
                commentCountText.visibility = View.VISIBLE
                commentCountText.text = it.commentCount.toString()
            }
        }

        // Inside the bind function in PostView class, after setting the author's name
        authorName.setOnClickListener {
            if (post != null) {
                post.author?.id?.let { userId -> onAuthorClickListener.onAuthorClick(userId) }
            }
        }

        authorAvatar.setOnClickListener {
            if (post != null) {
                post.author?.id?.let { userId -> onAuthorClickListener.onAuthorClick(userId) }
            }
        }

        commentButton.setOnClickListener {
            post?.let { item -> onCommentClickListener.onCommentClick(item) }
        }
    }

    // Add this interface to your PostView class
    interface OnAuthorClickListener {
        fun onAuthorClick(userId: String)
    }

    interface OnCommentClickListener {
        fun onCommentClick(post: Post)
    }

    fun onDetachedFromWindow() {
        Log.d("PostView", "onDetachedFromWindow called for ViewHolder: $this")
        // Release the CustomVideoPlayer when the view is detached
        customVideoPlayer.releasePlayer()
    }
}
