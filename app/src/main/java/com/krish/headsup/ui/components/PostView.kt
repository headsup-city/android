package com.krish.headsup.ui.components

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.krish.headsup.R
import com.krish.headsup.model.Post
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource

class PostView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val authorName: TextView
    private val postText: TextView
    private val postImage: ImageView
    private val postVideo: StyledPlayerView
    private var player: ExoPlayer? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_post, this, true)
        orientation = VERTICAL

        authorName = findViewById(R.id.authorName)
        postText = findViewById(R.id.postText)
        postImage = findViewById(R.id.postImage)
        postVideo = findViewById(R.id.postVideo)
    }

    fun bind(post: Post) {
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // Release the ExoPlayer when the view is detached
        player?.release()
    }
}
