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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentHomeBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.HomeAdapter
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.utils.LocationCallback
import com.krish.headsup.utils.LocationUtils
import com.krish.headsup.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), LocationCallback, HomeAdapter.OnPostClickListener, PostView.OnAuthorClickListener {

    private var latitude: Double? = null
    private var longitude: Double? = null

    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("DebugPostNotLoading", "onCreateView")
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
            // Handle button click
        }

        // Set up the SwipeRefreshLayout for pull-to-refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            if (latitude != null && longitude != null) {
                checkLocationPermission()
            }
        }

        val adapter = HomeAdapter(this, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        viewModel.currentPostResult.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow?.collectLatest { pagingData ->
                    // Update the adapter's data with the new posts and notify the adapter
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    private fun checkLocationPermission() {
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
    }

    override fun onLocationFailure() {
        // Handle the case when location is not available
        Log.d("DebugPostNotLoading", "onLocationFailure")
        binding.swipeRefreshLayout.isRefreshing = false
    }

    override fun onPostClick(post: Post, navHostViewId: Int) {
        Log.d("onPostClick", post.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
