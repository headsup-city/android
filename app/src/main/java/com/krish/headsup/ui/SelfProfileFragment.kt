package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.adapters.ProfilePostAdapter
import com.krish.headsup.adapters.SelfProfileHeaderAdapter
import com.krish.headsup.databinding.FragmentSelfProfileBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.viewmodel.SelfProfileViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelfProfileFragment :
    Fragment(),
    PostView.OnCommentClickListener,
    PostView.OnAuthorClickListener,
    PostView.OnLikeButtonClickListener {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: SelfProfileViewModel by viewModels()

    private lateinit var binding: FragmentSelfProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelfProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            profileRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            retryButton.setOnClickListener { viewModel.retry() }
        }

        sharedViewModel.user.observe(viewLifecycleOwner) {
            user ->
            viewModel.initializeScreen(user)
        }

        val headerAdapter = SelfProfileHeaderAdapter(
            onEditProfileClick = { onPressEditProfile() },
            onSettingButtonClick = { onPressSetting() },
            sharedViewModel = sharedViewModel,
        )

        val postAdapter = ProfilePostAdapter(
            this,
            this,
            this,
            viewLifecycleOwner,
            sharedViewModel,
        )

        val concatAdapter = ConcatAdapter(headerAdapter, postAdapter)
        binding.profileRecyclerview.adapter = concatAdapter

        viewModel.isLoading.observe(viewLifecycleOwner) {
            binding.retryButton.visibility = if (it && postAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.apiError.observe(viewLifecycleOwner) {
            binding.retryButton.visibility = if (it != null && postAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading && postAdapter.itemCount == 0) {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.retryButton.visibility = View.GONE
            } else {
                binding.loadingProgressBar.visibility = View.GONE
                // Remove this line, let the apiError observe block handle the visibility of retryButton
                binding.retryButton.visibility = View.GONE
            }
        }

        viewModel.apiError.observe(viewLifecycleOwner) { isError ->
            if (isError != null && postAdapter.itemCount == 0) {
                binding.retryButton.visibility = View.VISIBLE
                binding.loadingProgressBar.visibility = View.GONE
            } else {
                binding.retryButton.visibility = View.GONE
            }
        }

        viewModel.posts.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    postAdapter.submitData(pagingData)
                }
            }
        }

        postAdapter.addLoadStateListener { loadStates ->
            // Handling initial loading state
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    binding.retryButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.emptyPostsTextView.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    binding.retryButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.emptyPostsTextView.visibility = if (postAdapter.itemCount == 0) View.VISIBLE else View.GONE
                }
                is LoadState.Error -> {
                    binding.retryButton.visibility = View.VISIBLE
                    binding.loadingProgressBar.visibility = View.GONE
                    binding.emptyPostsTextView.visibility = View.GONE
                }
            }

            // Handling loading more posts state
            when (loadStates.append) {
                is LoadState.Loading -> {
                    // Show loading indicator at the bottom for loading more posts
                }
                is LoadState.NotLoading -> {
                    // Hide loading indicator at the bottom
                }
                is LoadState.Error -> {
                    // Show error message at the bottom
                }
            }

            // Handling refreshing state
            when (loadStates.prepend) {
                is LoadState.Loading -> {
                    // Show refreshing indicator
                }
                is LoadState.NotLoading -> {
                    // Hide refreshing indicator
                }
                is LoadState.Error -> {
                    // Show error message
                }
            }
        }
    }

    override fun onCommentClick(post: Post) {
        val navController = NavHostFragment.findNavController(this)
        when (post.postType) {
            "PRIMARY" -> {
                if (post.attachment?.uri.isNullOrEmpty()) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToTextPostFragment(post)
                    navController.navigate(action)
                } else {
                    val action = ProfileFragmentDirections.actionProfileFragmentToImagePostFragment(post)
                    navController.navigate(action)
                }
            }
            "SHORT" -> {
                val action = ProfileFragmentDirections.actionProfileFragmentToVideoPostFragment(post)
                navController.navigate(action)
            }
        }
    }

    override fun onAuthorClick(userId: String) {
        // Implement the logic for handling post clicks here
    }

    override fun onLikeButtonClick(postId: String, onResult: (Boolean) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.likePost(postId)
            onResult(result)
        }
    }

    override fun onUnlikeButtonClick(postId: String, onResult: (Boolean) -> Unit) {
        viewLifecycleOwner.lifecycleScope.launch {
            val result = viewModel.unlikePost(postId)
            onResult(result)
        }
    }

    private fun onPressSetting() {
        val action = SelfProfileFragmentDirections.actionProfileFragmentToSettingFragment()
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
    }

    private fun onPressEditProfile() {
        val action = SelfProfileFragmentDirections.actionProfileFragmentToEditProfileFragment()
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
    }
}
