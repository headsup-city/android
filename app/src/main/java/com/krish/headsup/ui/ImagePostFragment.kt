package com.krish.headsup.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.krish.headsup.R
import com.krish.headsup.adapters.CommentAdapter
import com.krish.headsup.databinding.FragmentImagePostBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.ImagePostViewModel
import com.krish.headsup.viewmodel.ImagePostViewModel_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagePostFragment : Fragment() {

    @Inject
    lateinit var imagePostViewModelFactory: ImagePostViewModel_AssistedFactory
    private lateinit var binding: FragmentImagePostBinding
    private val commentAdapter = CommentAdapter()

    private lateinit var postImageView: ImageView

    private lateinit var displayMetrics: DisplayMetrics
    private var screenHeight: Int = 0
    private var maxHeight: Int = 0

    private val viewModel: ImagePostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ImagePostViewModel::class.java)) {
                    return imagePostViewModelFactory.create(SavedStateHandle(mapOf("post" to requireArguments().getParcelable<Post>("post")))) as T
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
        binding = FragmentImagePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val commentSendButton: ImageButton = view.findViewById(R.id.comment_send_button)
        postImageView = binding.postImage

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val navController = NavHostFragment.findNavController(this)

        displayMetrics = resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        maxHeight = (0.3 * screenHeight).toInt()

        setupRecyclerView()
        observeViewModel()

        binding.commentInput.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                hideKeyboard(v)
            }
        }

        binding.commentsRecyclerView.setOnTouchListener { _, _ ->
            binding.commentInput.clearFocus()
            false
        }

        commentSendButton.setOnClickListener {
            sendComment()
        }

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        commentAdapter.addLoadStateListener { loadState ->
            // Show the progress bar while data is being loaded
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }

            // Update the visibility of the noCommentsPrompt based on the itemCount and load state
            val noCommentsPrompt: TextView? = view.findViewById(R.id.noCommentsPrompt)
            if (commentAdapter.itemCount > 0) {
                noCommentsPrompt?.visibility = View.GONE
            } else {
                // Only show the noCommentsPrompt if the data has finished loading and there are no items
                if (loadState.refresh !is LoadState.Loading) {
                    noCommentsPrompt?.visibility = View.VISIBLE
                } else {
                    noCommentsPrompt?.visibility = View.GONE
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun hideKeyboard(view: View) {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun sendComment() {
        val commentText = binding.commentInput.text.toString().trim()
        if (commentText.isNotEmpty()) {
            // Call the function in your ViewModel to send the comment
            // viewModel.sendComment(commentText)

            // Clear the comment input field
            binding.commentInput.setText("")

            // Hide the keyboard
            hideKeyboard(binding.commentInput)
        } else {
            // Show a message when the comment field is empty
            hideKeyboard(binding.commentInput)
        }
    }

    private fun observeViewModel() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            binding.apply {
                postTextContent.text = post.caption
                authorName.text = post.author?.name
                // Load the author's avatar using Glide
                if (CustomAvatarImageView.defaultAvatar == null) {
                    CustomAvatarImageView.defaultAvatar = context?.let { ContextCompat.getDrawable(it, R.drawable.default_avatar) }
                }
                GlideApp.with(requireContext())
                    .load(post.author?.avatarUri)
                    .signature(CustomCacheKeyGenerator(post.author?.avatarUri ?: ""))
                    .placeholder(CustomAvatarImageView.defaultAvatar)
                    .circleCrop()
                    .into(authorAvatar)
                GlideApp.with(requireContext())
                    .asBitmap()
                    .load(post.attachment?.uri)
                    .listener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: com.bumptech.glide.request.target.Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: com.bumptech.glide.request.target.Target<Bitmap>?, dataSource: com.bumptech.glide.load.DataSource?, isFirstResource: Boolean): Boolean {
                            resource?.let { bitmap ->
                                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()

                                val layoutParams = postImageView.layoutParams
                                layoutParams.height = (postImageView.width / aspectRatio).toInt()
                                    .coerceAtMost(maxHeight)
                                postImageView.layoutParams = layoutParams
                            }
                            return false
                        }
                    })
                    .centerInside()
                    .into(postImageView)

                postDate.text = getRelativeTime(post.createdAt, requireContext())
            }
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
}
