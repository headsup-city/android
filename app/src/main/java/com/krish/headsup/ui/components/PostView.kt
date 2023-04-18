package com.krish.headsup.ui.components

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.krish.headsup.R
import com.krish.headsup.model.Post
import com.krish.headsup.utils.dpToPx
import com.krish.headsup.utils.getRelativeTime

class PostView(itemView: View, private val screenWidth: Int) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context
    private val authorAvatar: CustomAvatarImageView = itemView.findViewById(R.id.authorAvatar)
    private val authorName: TextView = itemView.findViewById(R.id.authorName)
    private val postDate: TextView = itemView.findViewById(R.id.postDate)
    private val postImage: ImageView = itemView.findViewById(R.id.postImage)
    private val postText: TextView = itemView.findViewById(R.id.postText)
    private val postVideo: StyledPlayerView = itemView.findViewById(R.id.postVideo)
    private var player: ExoPlayer? = null

    private val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    private val likeCountText: TextView = itemView.findViewById(R.id.likeCount)
    private val commentButton: ImageView = itemView.findViewById(R.id.commentButton)
    private val commentCountText: TextView = itemView.findViewById(R.id.commentCount)
    private val shareButton: ImageView = itemView.findViewById(R.id.shareButton)


    fun bind(post: Post) {

        // Load the author's avatar using Glide
        if (CustomAvatarImageView.defaultAvatar == null) {
            CustomAvatarImageView.defaultAvatar = ContextCompat.getDrawable(context, R.drawable.default_avatar)
        }

        Glide.with(context)
            .load(post.author?.avatarUri)
            .placeholder(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(authorAvatar)

        authorName.text = post.author?.name
        postDate.text = getRelativeTime(post.createdAt, context)
        postText.text = post.caption

        if (post.caption.isNullOrEmpty()) {
            postText.visibility = View.GONE
        }

        when (post.postType) {
            "PRIMARY" -> {
                if (!post.attachment?.uri.isNullOrEmpty()) {
                    postImage.visibility = View.VISIBLE

                    // Calculate the height based on the aspect ratio
                    val aspectRatio = post.attachment?.height?.toFloat()?.div(post.attachment?.width ?: 1) ?: 0f
                    val calculatedHeight = (screenWidth * aspectRatio).toInt()

                    // Set the maxHeight constraint
                    val maxHeightInPx = dpToPx(context, 350f)
                    val finalHeight = if (calculatedHeight > maxHeightInPx) maxHeightInPx else calculatedHeight

                    // Update the ImageView's height
                    val layoutParams = postImage.layoutParams
                    layoutParams.height = finalHeight
                    postImage.layoutParams = layoutParams

                    // Load the image using Glide
                    Glide.with(context)
                        .load(post.attachment?.uri)
                        .centerCrop()
                        .into(postImage)
                }
            }

            "short" -> {
                postVideo.visibility = View.VISIBLE

                // Initialize ExoPlayer
                player = ExoPlayer.Builder(context).build()
                postVideo.player = player

                // Create a MediaSource
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
                val mediaItem = MediaItem.fromUri(Uri.parse(post.attachment?.uri))
                val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(mediaItem)

                // Set the MediaSource and prepare the player
                player?.setMediaSource(mediaSource)
                player?.prepare()
                player?.playWhenReady = true
            }
        }

        if (post.likeCount!! > 0) {
            likeCountText.visibility = View.VISIBLE
            likeCountText.text = post.likeCount.toString()
        }

        if (post.commentCount!! > 0) {
            commentCountText.visibility = View.VISIBLE
            commentCountText.text = post.commentCount.toString()
        }
    }

    fun onDetachedFromWindow() {
        // Release the ExoPlayer when the view is detached
        player?.release()
    }
}
