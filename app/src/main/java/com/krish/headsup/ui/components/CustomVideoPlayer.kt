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
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.krish.headsup.R
import com.krish.headsup.utils.ExoPlayerUtil
import com.krish.headsup.utils.PreferenceManager

class CustomVideoPlayer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val playerView: StyledPlayerView
    private val progressBar: ProgressBar
    private val playButton: ImageView
    private val muteButton: ImageView
    private val unmuteButton: ImageView
    private var player: ExoPlayer? = null
    private var isReplaying = false
    private var showMuteButtons = true

    init {
        LayoutInflater.from(context).inflate(R.layout.fragment_custom_video_player, this, true)

        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomVideoPlayer)
            showMuteButtons = typedArray.getBoolean(R.styleable.CustomVideoPlayer_showMuteButtons, true)
            typedArray.recycle()
        }

        playerView = findViewById(R.id.postVideo)
        progressBar = findViewById(R.id.videoLoadingProgressBar)
        playButton = findViewById(R.id.playButton)
        muteButton = findViewById(R.id.muteButton)
        unmuteButton = findViewById(R.id.unmuteButton)

        updateMuteButtonVisibility()

        muteButton.setOnClickListener {
            PreferenceManager.setMuteState(context, false)
            syncMuteState()
        }

        unmuteButton.setOnClickListener {
            PreferenceManager.setMuteState(context, true)
            syncMuteState()
        }

        syncMuteState()

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

    private fun syncMuteState() {
        player?.volume = if (PreferenceManager.getMuteState(context)) 0f else 1f
        updateMuteButtonVisibility()
    }

    private fun updateMuteButtonVisibility() {
        if (!showMuteButtons) {
            muteButton.visibility = View.GONE
            unmuteButton.visibility = View.GONE
            return
        }

        val isMuted = PreferenceManager.getMuteState(context)
        muteButton.visibility = if (isMuted) View.VISIBLE else View.GONE
        unmuteButton.visibility = if (isMuted) View.GONE else View.VISIBLE
    }

    fun setVideoUri(uri: String) {
        // Initialize ExoPlayer
        player = ExoPlayerUtil.createExoPlayer(context)
        playerView.player = player

        player?.addListener(CustomPlayerListener())

        // Set the MediaItem directly on the ExoPlayer instance
        val mediaItem = MediaItem.fromUri(uri)
        player?.setMediaItem(mediaItem)

        syncMuteState()

        // Prepare the player
        player?.prepare()
        player?.playWhenReady = true
    }

    private inner class CustomPlayerListener : Player.Listener {

        override fun onPlaybackStateChanged(state: Int) {
            when (state) {
                Player.STATE_BUFFERING -> {
                    progressBar.visibility = View.VISIBLE
                    playButton.visibility = View.GONE
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

    fun resetPlayer() {
        releasePlayer()
        progressBar.visibility = View.VISIBLE
    }

    fun releasePlayer() {
        // Release the ExoPlayer when the view is detached
        player?.release()
    }
}
