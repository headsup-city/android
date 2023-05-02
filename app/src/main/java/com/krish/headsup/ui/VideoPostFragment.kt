package com.krish.headsup.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView
import com.krish.headsup.R
import com.krish.headsup.adapters.CommentAdapter
import com.krish.headsup.databinding.FragmentVideoPostBinding
import com.krish.headsup.model.Post
import com.krish.headsup.viewmodel.VideoPostViewModel
import com.krish.headsup.viewmodel.VideoPostViewModel_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class VideoPostFragment : Fragment() {

    @Inject
    lateinit var videoPostViewModelFactory: VideoPostViewModel_AssistedFactory
    private lateinit var binding: FragmentVideoPostBinding
    private val commentAdapter = CommentAdapter()
    private var player: ExoPlayer? = null

    private val viewModel: VideoPostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(VideoPostViewModel::class.java)) {
                    return videoPostViewModelFactory.create(SavedStateHandle(mapOf("post" to requireArguments().getParcelable<Post>("post")))) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

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

        val backButton: ImageButton = requireView().findViewById(R.id.backButton)
        val navController = NavHostFragment.findNavController(this)
        val commentButton: ImageButton = requireView().findViewById(R.id.commentButton)

        observeViewModel()

        commentButton.setOnClickListener { showCommentsBottomSheet() }

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        setupExoPlayer()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showCommentsBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val contentView = layoutInflater.inflate(R.layout.video_post_comments, binding.root.findViewById(R.id.coordinatorLayout), false)
        bottomSheetDialog.setContentView(contentView)

        val commentsRecyclerView = bottomSheetDialog.findViewById<RecyclerView>(R.id.commentsRecyclerView)
        val commentInput = bottomSheetDialog.findViewById<EditText>(R.id.comment_input)
        val commentSendButton = bottomSheetDialog.findViewById<ImageButton>(R.id.comment_send_button)
        val loadingCommentsProgressBar = bottomSheetDialog.findViewById<ProgressBar>(R.id.loadingComments)
        val noCommentsText: TextView? = bottomSheetDialog.findViewById(R.id.noCommentsText)

        // Set the height of the content view to 70% of the screen height
        val windowHeight = Resources.getSystem().displayMetrics.heightPixels
        val maxHeight = (windowHeight * 0.7).toInt()

        val params = contentView.findViewById<LinearLayout>(R.id.commentsContainer).layoutParams
        params.height = maxHeight
        contentView.findViewById<LinearLayout>(R.id.commentsContainer).layoutParams = params

        val bottomSheetBehavior = BottomSheetBehavior.from(contentView.findViewById<MaterialCardView>(R.id.commentsBottomSheet))
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Set up RecyclerView, adapter, and other components for comments
        commentsRecyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }

        commentInput?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        commentAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                loadingCommentsProgressBar?.visibility = View.VISIBLE
                noCommentsText?.visibility = View.GONE
            } else {
                loadingCommentsProgressBar?.visibility = View.GONE
                if (commentAdapter.itemCount > 0) {
                    noCommentsText?.visibility = View.GONE
                } else {
                    noCommentsText?.visibility = View.VISIBLE
                    noCommentsText?.post {
                        val textHeight = noCommentsText.height
                        val containerHeight = contentView.findViewById<LinearLayout>(R.id.commentsContainer).height
                        noCommentsText.translationY = (containerHeight / 2f) - (textHeight / 2f)
                    }
                }
            }
        }

        commentsRecyclerView?.setOnTouchListener { _, _ ->
            commentInput?.clearFocus()
            false
        }

        commentSendButton?.setOnClickListener {
            sendComment(commentInput, bottomSheetDialog)
        }

        commentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                val noCommentsText: TextView? = bottomSheetDialog.findViewById(R.id.noCommentsText)
                if (commentAdapter.itemCount > 0) {
                    noCommentsText?.visibility = View.GONE
                } else {
                    noCommentsText?.visibility = View.VISIBLE
                }
            }
        })

        bottomSheetDialog.show()
    }

    private fun setupExoPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.videoPlayer.player = player
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun sendComment(commentInput: EditText?, bottomSheetDialog: BottomSheetDialog) {
        val commentText = commentInput?.text.toString().trim()
        if (commentText.isNotEmpty()) {
            // Call the function in your ViewModel to send the comment
            // viewModel.sendComment(commentText)

            // Clear the comment input field
            commentInput?.setText("")

            // Hide the keyboard
            commentInput?.let { hideKeyboard(it) }
        } else {
            // Show a message when the comment field is empty
            commentInput?.let { hideKeyboard(it) }
        }
    }

    private fun observeViewModel() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            binding.postCaption.text = post.caption
            binding.authorName.text = post.author?.name

            val dataSourceFactory = DefaultHttpDataSource.Factory()
            val mediaSourceFactory = ProgressiveMediaSource.Factory(dataSourceFactory)
            val mediaItem = MediaItem.fromUri(post.attachment?.uri ?: "")
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
