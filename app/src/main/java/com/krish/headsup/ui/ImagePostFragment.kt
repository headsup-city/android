package com.krish.headsup.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.DataSource
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
import kotlin.math.min
import kotlin.math.max

@AndroidEntryPoint
class ImagePostFragment : Fragment() {

    @Inject
    lateinit var imagePostViewModelFactory: ImagePostViewModel_AssistedFactory
    private lateinit var binding: FragmentImagePostBinding
    private val commentAdapter = CommentAdapter()

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
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

        val commentSendButton: ImageButton = requireView().findViewById(R.id.comment_send_button)
        postImageView = binding.postImage

        val backButton: ImageButton = requireView().findViewById(R.id.backButton)
        val navController = NavHostFragment.findNavController(this)

        displayMetrics = resources.displayMetrics
        screenHeight = displayMetrics.heightPixels
        maxHeight = (0.6 * screenHeight).toInt()



        setupRecyclerView()
        observeViewModel()


        scaleGestureDetector = ScaleGestureDetector(requireContext(), ScaleListener())

        postImageView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
            true
        }

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
//            viewModel.sendComment(commentText)

            // Clear the comment input field
            binding.commentInput.setText("")

            // Hide the keyboard
            hideKeyboard(binding.commentInput)
        } else {
            // Show a message when the comment field is empty
            hideKeyboard(binding.commentInput)
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = max(0.1f, min(scaleFactor, 5.0f))

            postImageView.scaleX = scaleFactor
            postImageView.scaleY = scaleFactor
            return true
        }
    }

    private fun observeViewModel() {
        viewModel.post.observe(viewLifecycleOwner) { post ->
            binding.apply {
                postTextContent.text = post.caption
                authorName.text = post.author?.name
                // You can set other post details like author's avatar, post date, etc.
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
//                                val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
//
//                                val layoutParams = postImageView.layoutParams
//                                layoutParams.height = Math.min((postImageView.width / aspectRatio).toInt(), maxHeight)
//                                postImageView.layoutParams = layoutParams
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
                Log.d("TextPostFragment", "Received comments: $pagingData")
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


