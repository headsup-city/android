package com.krish.headsup.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.krish.headsup.R

class CustomVideoPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val playerView: StyledPlayerView
    private val progressBar: ProgressBar
    private val playButton: ImageView
    private var player: ExoPlayer? = null
    private var isReplaying = false

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_custom_video_player, this, true)

        playerView = findViewById(R.id.postVideo)
        progressBar = findViewById(R.id.videoLoadingProgressBar)
        playButton = findViewById(R.id.playButton)

        playButton.setOnClickListener {
            player?.let {
                if (it.playbackState == Player.STATE_ENDED) {
                    it.seekTo(0) // Seek back to the beginning
                    isReplaying = true
                }
                it.playWhenReady = true
                playButton.visibility = View.GONE
            }
        }
    }

    fun setVideoUri(uri: String) {
        // Initialize ExoPlayer
        player = ExoPlayer.Builder(context).build()
        playerView.player = player

        player?.addListener(CustomPlayerListener())

        // Create a MediaSource
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
        val mediaItem = MediaItem.fromUri(uri)
        val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        // Set the MediaSource and prepare the player
        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true
    }

    private inner class CustomPlayerListener : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> {
                    if (!isReplaying) {
                        progressBar.visibility = View.VISIBLE
                    }
                }
                Player.STATE_ENDED -> {
                    playButton.visibility = View.VISIBLE
                    isReplaying = false
                }
                else -> {
                    progressBar.visibility = View.GONE
                    playButton.visibility = View.GONE
                }
            }
        }
    }

    fun releasePlayer() {
        // Release the ExoPlayer when the view is detached
        player?.release()
    }
}
