package com.krish.headsup.ui.components

import android.content.Context
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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

class PostView(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context
    private val authorAvatar: ImageView = itemView.findViewById(R.id.authorAvatar)
    private val authorName: TextView = itemView.findViewById(R.id.authorName)
    private val postImage: ImageView = itemView.findViewById(R.id.postImage)
    private val postText: TextView = itemView.findViewById(R.id.postText)
    private val postVideo: StyledPlayerView = itemView.findViewById(R.id.postVideo)
    private var player: ExoPlayer? = null

    fun bind(post: Post) {

        // Load the author's avatar using Glide
        Glide.with(context)
            .load(post.author?.avatar)
            .placeholder(R.drawable.default_avatar)
            .circleCrop()
            .into(authorAvatar)

        authorName.text = post.author?.name
        postText.text = post.caption

        when (post.postType) {
            "text" -> {
                postImage.visibility = View.GONE
                postVideo.visibility = View.GONE
            }

            "photo" -> {
                postImage.visibility = View.VISIBLE
                postVideo.visibility = View.GONE

                // Load the image using Glide
                Glide.with(context)
                    .load(post.imageUri)
                    .centerCrop()
                    .into(postImage)
            }

            "video" -> {
                postImage.visibility = View.GONE
                postVideo.visibility = View.VISIBLE

                // Initialize ExoPlayer
                player = ExoPlayer.Builder(context).build()
                postVideo.player = player

                // Create a MediaSource
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
                val mediaItem = MediaItem.fromUri(Uri.parse(post.imageUri))
                val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(mediaItem)

                // Set the MediaSource and prepare the player
                player?.setMediaSource(mediaSource)
                player?.prepare()
                player?.playWhenReady = true
            }
        }
    }

    fun onDetachedFromWindow() {
        // Release the ExoPlayer when the view is detached
        player?.release()
    }
}