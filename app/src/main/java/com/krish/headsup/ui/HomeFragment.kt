package com.krish.headsup.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentHomeBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.PostPagingDataAdapter
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.utils.LocationCallback
import com.krish.headsup.utils.LocationUtils
import com.krish.headsup.viewmodel.HomeViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment :
    Fragment(),
    LocationCallback,
    PostView.OnCommentClickListener,
    PostView.OnAuthorClickListener,
    PostView.OnLikeButtonClickListener {

    private var latitude: Double? = null
    private var longitude: Double? = null

    private var getLocationRetryCount: Int = 0

    private val MAX_GET_LOCATION_RETRY_COUNT: Int = 5

    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private var arePostsEmpty: Boolean = true

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, get the location
                val locationUtils = LocationUtils(requireContext(), this)
                locationUtils.getLiveLocation()
            } else {
                // Handle the case when permission is denied
            }
        }

        checkLocationPermission()

        val toolbarTitle = view.findViewById<TextView>(R.id.toolbarTitleText)
        val toolbarTitleIconRight = view.findViewById<ImageView>(R.id.toolbarTitleIconRight)
        val convoButton = view.findViewById<ImageView>(R.id.toolbarRightButton)

        toolbarTitle.text = getString(R.string.near_you)
        toolbarTitleIconRight.visibility = View.VISIBLE

        // Set an OnClickListener for the optional convo button
        convoButton.visibility = View.VISIBLE
        convoButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToConversationFragment()
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(action)
        }

        // Set up the SwipeRefreshLayout for pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (latitude != null && longitude != null) {
                checkLocationPermission()
            }
        }

        val adapter = PostPagingDataAdapter(
            this,
            this,
            this,
            viewLifecycleOwner,
            sharedViewModel
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.retryButton.setOnClickListener {
            checkLocationPermission()
        }

        viewModel.currentPostResult.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow?.collectLatest { pagingData ->
                    // Update the adapter's data with the new posts and notify the adapter
                    adapter.submitData(pagingData)
                    if (adapter.itemCount == 0) {
                        arePostsEmpty = true
                    } else {
                        arePostsEmpty = false
                        binding.retryButton.visibility = View.GONE
                    }
                }
            }
        }

        adapter.addLoadStateListener { loadState ->
            // Show loading indicator at center of the screen or at the top depending on posts' presence.
            val isRefreshing = loadState.refresh is LoadState.Loading
            binding.swipeRefreshLayout.isRefreshing = isRefreshing && !arePostsEmpty
            binding.loadingProgressBar.visibility = if (isRefreshing && arePostsEmpty) View.VISIBLE else View.GONE

            // If there is an error, show a toast or the retry button depending on posts' presence.
            val isError = loadState.refresh is LoadState.Error
            if (isError) {
                if (arePostsEmpty) {
                    // There was an error refreshing (i.e., initial load), show the retry button and hide the ProgressBar
                    binding.retryButton.visibility = View.VISIBLE
                    binding.loadingProgressBar.visibility = View.GONE
                } else {
                    // Posts are present, hide the retry button and ProgressBar, and show a toast
                    binding.retryButton.visibility = View.GONE
                    binding.swipeRefreshLayout.isRefreshing = false
                    Toast.makeText(context, "An error occurred while refreshing", Toast.LENGTH_SHORT).show()
                }
            } else {
                // No error, hide the retry button and both ProgressBars
                binding.retryButton.visibility = View.GONE
                binding.loadingProgressBar.visibility = View.GONE
                binding.swipeRefreshLayout.isRefreshing = false
            }
        }
    }

    private fun checkLocationPermission() {
        Log.d("DebugSelf", "Getting location")
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission granted, get the location here
                val locationUtils = LocationUtils(requireContext(), this)
                locationUtils.getLiveLocation()
            }
            else -> {
                // Request the location permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onLocationSuccess(latitude: Double, longitude: Double) {
        // Use latitude and longitude for the API call
        this.latitude = latitude
        this.longitude = longitude
        viewModel.loadPosts(latitude, longitude)
        binding.swipeRefreshLayout.isRefreshing = false
        getLocationRetryCount = 0
    }

    override fun onLocationFailure() {
        Log.e("DebugPostNotLoading", "onLocationFailure")
        // Retry location fetching automatically for a few times
        if (getLocationRetryCount < MAX_GET_LOCATION_RETRY_COUNT) {
            getLocationRetryCount++
            checkLocationPermission()
        } else {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.loadingProgressBar.visibility = View.GONE
            if (arePostsEmpty) {
                binding.retryButton.visibility = View.VISIBLE // Show the retry button
            } else {
                binding.retryButton.visibility = View.GONE // Hide the retry button
            }
            getLocationRetryCount = 0
        }
    }

    override fun onCommentClick(post: Post) {
        val navController = NavHostFragment.findNavController(this)
        when (post.postType) {
            "PRIMARY" -> {
                if (post.attachment?.uri.isNullOrEmpty()) {
                    val action = HomeFragmentDirections.actionHomeFragmentToTextPostFragment(post)
                    navController.navigate(action)
                } else {
                    val action = HomeFragmentDirections.actionHomeFragmentToImagePostFragment(post)
                    navController.navigate(action)
                }
            }
            "SHORT" -> {
                val action = HomeFragmentDirections.actionHomeFragmentToVideoPostFragment(post)
                navController.navigate(action)
            }
        }
    }

    override fun onAuthorClick(userId: String) {
        val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment(userId)
        val navController = NavHostFragment.findNavController(this)
        navController.navigate(action)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
