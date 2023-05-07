package com.krish.headsup.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentProfileBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.ui.components.PostPagingDataAdapter
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.ProfileViewModel
import com.krish.headsup.viewmodel.ProfileViewModel_AssistedFactory
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PostView.OnCommentClickListener, PostView.OnAuthorClickListener, PostView.OnLikeButtonClickListener {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel_AssistedFactory

    private val sharedViewModel: SharedViewModel by activityViewModels()

    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    return profileViewModelFactory.create(
                        SavedStateHandle(
                            mapOf(
                                "userId" to (
                                    arguments?.getString("userId")
                                        ?: ""
                                    )
                            )
                        ),
                        sharedViewModel
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        sharedViewModel.user.observe(
            viewLifecycleOwner,
            Observer { user ->
                // Log user data here

                // Check if the userId is in user.following and update button visibility accordingly
                val isFollowing = user.following?.contains(
                    arguments?.getString("userId")
                        ?: ""
                )

                binding.followButton.visibility = if (isFollowing == true) View.GONE else View.VISIBLE
                binding.followedButton.visibility = if (isFollowing == true) View.VISIBLE else View.GONE
            }
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostPagingDataAdapter(this, this, this, viewLifecycleOwner, sharedViewModel)
        binding.profileRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecyclerview.adapter = adapter

        val backButton: ImageButton = view.findViewById(R.id.backButton)
        val navController = NavHostFragment.findNavController(this)

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.followButton.setOnClickListener {
            followUser()
        }

        binding.followedButton.setOnClickListener {
            unFollowUser()
        }

        // Observe the user LiveData from the ViewModel
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                // Update the UI with the user data
                // For example, if you have a TextView with ID 'user_name', you can set its text like this:
                // binding.userName.text = user.name
                binding.userName.text = user.name

                GlideApp.with(requireContext())
                    .load(user.avatarUri)
                    .signature(CustomCacheKeyGenerator(user.avatarUri ?: ""))
                    .placeholder(CustomAvatarImageView.defaultAvatar)
                    .circleCrop()
                    .into(binding.authorAvatar) // Use the ImageView from the binding
            }
        }

        viewModel.posts.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow?.collectLatest { pagingData ->
                    // Update the adapter's data with the new posts and notify the adapter
                    adapter.submitData(pagingData)
                }
            }
        }

        Log.d("DebugSelf", "Inside")

        sharedViewModel.user.observe(
            viewLifecycleOwner,
            Observer { user ->
                // Log user data here
            }
        )
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

    private fun followUser() {
        // Get the user ID from the ViewModel's user LiveData
        val userIdToFollow = viewModel.user.value?.id ?: return
        binding.followButton.visibility = View.GONE
        binding.followedButton.visibility = View.VISIBLE
        lifecycleScope.launch {
            val success = viewModel.followUser(userIdToFollow)
            if (success) {
                Log.d("DebugSelf", "FollowUser success")
            } else {
                Log.d("DebugSelf", "FollowUser failed")
                binding.followButton.visibility = View.VISIBLE
                binding.followedButton.visibility = View.GONE
            }
        }
    }

    private fun unFollowUser() {
        // Get the user ID from the ViewModel's user LiveData
        val userIdToUnFollow = viewModel.user.value?.id ?: return
        binding.followedButton.visibility = View.GONE
        binding.followButton.visibility = View.VISIBLE
        lifecycleScope.launch {
            val success = viewModel.unFollowUser(userIdToUnFollow)
            if (success) {
                Log.d("DebugSelf", "unFollowUser success")
            } else {
                Log.d("DebugSelf", "unFollowUser failed")
                binding.followedButton.visibility = View.VISIBLE
                binding.followButton.visibility = View.GONE
            }
        }
    }
}
