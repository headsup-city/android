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
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentHomeBinding
import com.krish.headsup.ui.components.HomeAdapter
import com.krish.headsup.utils.LocationCallback
import com.krish.headsup.utils.LocationUtils
import com.krish.headsup.utils.Resource
import com.krish.headsup.viewmodel.HomeViewModel

class HomeFragment : Fragment(), LocationCallback {

    private lateinit var requestLocationPermissionLauncher: ActivityResultLauncher<String>
    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomeFragment", "onCreateView")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("DebugLoggingHomeFragment", "HomeFragment created")

        requestLocationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, get the location
                Log.d("HomeFragment", "Permission granted")
                val locationUtils = LocationUtils(requireContext(), this)
                locationUtils.getLiveLocation()
            } else {
                // Handle the case when permission is denied
                Log.d("HomeFragment", "Location permission denied")
            }
        }

        checkLocationPermission()

        val adapter = HomeAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val toolbarTitle = view.findViewById<TextView>(R.id.toolbarTitleText)
        val toolbarTitleIcon = view.findViewById<ImageView>(R.id.toolbarTitleIcon)
        val convoButton = view.findViewById<ImageView>(R.id.toolbarRightButton)

        toolbarTitle.text = getString(R.string.near_you)
        toolbarTitleIcon.visibility = View.VISIBLE

        // Set an OnClickListener for the optional convo button
        convoButton.visibility = View.VISIBLE
        convoButton.setOnClickListener {
            // Handle button click
        }

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    adapter.submitList(resource.data)
                }
                Resource.Status.ERROR -> {
                    // Handle the error case, e.g., show a toast or a message to the user
                }
                Resource.Status.LOADING -> {
                    // Handle the loading case, e.g., show a loading spinner
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
                Log.d("HomeFragment", "Request ACCESS_FINE_LOCATION")
                // Request the location permission
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    override fun onLocationSuccess(latitude: Double, longitude: Double) {
        // Use latitude and longitude for the API call
        Log.d("HomeFragment", "onLocationSuccess: latitude: $latitude,longitude: $longitude")
        viewModel.loadPosts(latitude, longitude)
    }

    override fun onLocationFailure() {
        // Handle the case when location is not available
        Log.d("HomeFragment", "onLocationFailure")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
