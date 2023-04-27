package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.krish.headsup.adapters.CommentAdapter
import com.krish.headsup.databinding.FragmentVideoPostBinding
import com.krish.headsup.viewmodel.VideoPostViewModelFactory
import com.krish.headsup.viewmodel.VideoPostViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoPostFragment : Fragment() {
    private val args: VideoPostFragmentArgs by navArgs()

    private lateinit var binding: FragmentVideoPostBinding
    private val viewModel: VideoPostViewModel by viewModels {
        VideoPostViewModelFactory(
            postRepository = get(),
            commentRepository = get(),
            tokenManager = get(),
            owner = this,
            defaultArgs = Bundle().apply { putString("postId", args.postId) }
        )
    }
    private val commentAdapter = CommentAdapter()
    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideoPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupExoPlayer()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupExoPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.videoPlayer.player = player
    }

    private fun setupRecyclerView() {
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            binding.caption.text = post.caption

            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
            val mediaItem = MediaItem.fromUri(post.imageUri ?: "")
            val mediaSource: MediaSource = mediaSourceFactory.createMediaSource(mediaItem)

            player?.setMediaSource(mediaSource)
            player?.prepare()
            player?.playWhenReady = true
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.comments.collectLatest { pagingData ->
                commentAdapter.submitData(pagingData)
            }
        }

        commentAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
    }
}
