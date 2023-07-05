package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.adapters.ProfileHeaderAdapter
import com.krish.headsup.adapters.ProfilePostAdapter
import com.krish.headsup.databinding.FragmentProfileBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.utils.ViewUtils
import com.krish.headsup.viewmodel.ProfileViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment :
    Fragment(),
    PostView.OnCommentClickListener,
    PostView.OnAuthorClickListener,
    PostView.OnLikeButtonClickListener,
    PostView.PostMenuActionListener {
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding

    private var isFollowingOtherUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewUtils = ViewUtils()

        val menuButton: ImageButton = view.findViewById(R.id.menuButton)
        val unblockButton: Button = view.findViewById(R.id.unblockButton)
        val userBlockedText: TextView = view.findViewById(R.id.userBlockedText)

        val userId = arguments?.getString("userId")
        val navController = NavHostFragment.findNavController(this)

        binding.apply {
            profileRecyclerview.layoutManager = LinearLayoutManager(requireContext())
            backButton.setOnClickListener { navController.navigateUp() }
            retryButton.setOnClickListener { viewModel.fetchUserData() }
            binding.menuButton.setOnClickListener { showPopupMenu() }
            viewUtils.increaseTouchableArea(backButton, R.dimen.size_32_button_inc)
        }

        // Add this at the end of onViewCreated
        sharedViewModel.user.observe(viewLifecycleOwner) { user ->
            if (user?.block?.contains(userId) == true) {
                // If the user is blocked, hide everything but the toolbar
                view.findViewById<RecyclerView>(R.id.profileRecyclerview).visibility = View.GONE
                view.findViewById<ImageView>(R.id.retryButton).visibility = View.GONE
                view.findViewById<TextView>(R.id.emptyPostsTextView).visibility = View.GONE

                // In the toolbar, hide the menu button and show the "This user is blocked" message and the "Unblock user" button
                menuButton.visibility = View.GONE
                unblockButton.visibility = View.VISIBLE
                userBlockedText.visibility = View.VISIBLE

                // When the "Unblock user" button is clicked, unblock the user
                unblockButton.setOnClickListener {
                    // Replace this with your actual function to unblock a user
                    lifecycleScope.launch {
                        val result = viewModel.unblockUser(userId!!)
                        if (result) {
                            val currentUser = sharedViewModel.user.value
                            currentUser?.let { user ->
                                val updatedBlockList = user.block?.filter { it != userId }
                                sharedViewModel.updateUser(user.copy(block = updatedBlockList as MutableList<String>?))
                            }
                        } else {
                            // handle error, e.g., show a Toast or SnackBar
                        }
                    }
                }
            } else {
                // If the user is not blocked, show everything and hide the "Unblock user" button
                view.findViewById<RecyclerView>(R.id.profileRecyclerview).visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.retryButton).visibility = View.VISIBLE

                menuButton.visibility = View.VISIBLE
                unblockButton.visibility = View.GONE
                userBlockedText.visibility = View.GONE
                if (userId != null) {
                    viewModel.initializeScreen(userId, user)
                } else {
                    viewModel.initializeScreen(user.id, user)
                }
            }
        }

        val headerAdapter = ProfileHeaderAdapter(
            onMessageClick = {
                val action = ProfileFragmentDirections.actionProfileFragmentToMessagingFragment(null, userId)
                navController.navigate(action)
            },
            onFollowClick = {
                followUser()
            },
            onUnfollowClick = {
                unFollowUser()
            }, onEditProfileClick = {},
            sharedViewModel = sharedViewModel,
        )

        val postAdapter = ProfilePostAdapter(
            this,
            this,
            this,
            this,
            viewLifecycleOwner,
            sharedViewModel,
        )

        val concatAdapter = ConcatAdapter(headerAdapter, postAdapter)
        binding.profileRecyclerview.adapter = concatAdapter

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.text = it.name
                headerAdapter.setData(user, isFollowingOtherUser)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) {
            if (it && postAdapter.itemCount == 0) {
                binding.loadingProgressBar.visibility = View.VISIBLE
                binding.retryButton.visibility = View.GONE
            } else {
                binding.loadingProgressBar.visibility = View.GONE
            }
        }

        viewModel.apiError.observe(viewLifecycleOwner) {
            if (it && postAdapter.itemCount == 0) {
                binding.retryButton.visibility = View.VISIBLE
                binding.loadingProgressBar.visibility = View.GONE
            } else {
                binding.retryButton.visibility = View.GONE
            }
        }

        viewModel.posts.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow?.collectLatest { pagingData ->
                    postAdapter.submitData(pagingData)
                }
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
            when (loadStates.refresh) {
                is LoadState.Loading -> {
                    binding.retryButton.visibility = View.GONE
                    binding.loadingProgressBar.visibility = View.VISIBLE
                    binding.emptyPostsTextView.visibility = View.GONE
                }
                is LoadState.NotLoading -> {
                    val isEmpty = postAdapter.itemCount == 0
                    if (isEmpty) {
                        // If it's not loading and there's no error, we hide both views
                        binding.retryButton.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.emptyPostsTextView.visibility = View.VISIBLE
                    } else {
                        // If there are items in the adapter, we hide both views
                        binding.retryButton.visibility = View.GONE
                        binding.loadingProgressBar.visibility = View.GONE
                        binding.emptyPostsTextView.visibility = View.GONE
                    }
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

        viewModel.followingUpdates.observe(viewLifecycleOwner) { followingUpdate ->
            val (followedUserId, follow) = followingUpdate
            sharedViewModel.user.value?.let { currentUser ->
                val updatedFollowingList = currentUser.following?.toMutableSet()?.apply {
                    if (follow) add(followedUserId) else remove(followedUserId)
                }?.toList()
                sharedViewModel.updateUser(currentUser.copy(following = updatedFollowingList))
            }
        }

        viewModel.isFollowing.observe(viewLifecycleOwner) { isFollowing ->
            isFollowingOtherUser = isFollowing
            postAdapter?.notifyDataSetChanged()
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

    private fun showPopupMenu() {
        val wrapper = ContextThemeWrapper(context, R.style.PostPopupMenu)
        val popup = PopupMenu(wrapper, binding.menuButton) // use the menuButton as the anchor view
        popup.inflate(R.menu.other_user_profile_menu)

        popup.show()

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.block -> {
                    val dialog = AlertDialog.Builder(requireContext())
                        .setMessage("Do you want to block this user?")
                        .setPositiveButton("Block") { _, _ ->
                            blockUser()
                        }
                        .setNegativeButton("Cancel", null)
                        .create()

                    dialog.show()
                    true
                }
                else -> false
            }
        }
    }

    private fun blockUser() {
        val userIdToBlock = viewModel.user.value?.id ?: return
        lifecycleScope.launch {
            try {
                val currentUser = sharedViewModel.user.value
                currentUser?.let { user ->
                    val updatedBlockList = user.block?.toMutableList()?.apply {
                        add(userIdToBlock)
                    }
                    sharedViewModel.updateUser(user.copy(block = updatedBlockList))
                }
                val result = viewModel.blockUser(userIdToBlock)
                if (!result) {
                    Toast.makeText(context, "Couldn't block, try again", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // handle error
            }
        }
    }

    override fun onAuthorClick(userId: String) {
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

    private fun followUser() {
        val userIdToFollow = viewModel.user.value?.id ?: return
        if (userIdToFollow != sharedViewModel.user.value?.id) {
            lifecycleScope.launch {
                viewModel.followUser(userIdToFollow)
            }
        }
    }

    private fun unFollowUser() {
        val userIdToFollow = viewModel.user.value?.id ?: return
        if (userIdToFollow != sharedViewModel.user.value?.id) {
            lifecycleScope.launch {
                viewModel.unFollowUser(userIdToFollow)
            }
        }
    }

    override fun onReportClick(post: Post) {
        lifecycleScope.launch {
            viewModel.reportPost(post.id)
        }
    }

    override fun onDeleteClick(post: Post) {
        lifecycleScope.launch {
            viewModel.deletePost(post.id)
        }
    }
}
