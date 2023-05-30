package com.krish.headsup.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.krish.headsup.databinding.FragmentEditProfileBinding
import com.krish.headsup.utils.NetworkUtil
import com.krish.headsup.viewmodel.EditProfileViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment() {
    private val viewModel: EditProfileViewModel by activityViewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var binding: FragmentEditProfileBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)

        binding.profilePictureOverlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.profilePictureOverlay.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_UP -> {
                    binding.profilePictureOverlay.visibility = View.INVISIBLE
                    // Start activity for result here to get new image
                }
            }
            true
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle back button click
        binding.backButton.setOnClickListener {
            // Go back to the previous screen
        }

        var initialUsername: String? = null

        viewModel.initializeScreen(sharedViewModel.user.value)

        binding.userName.doAfterTextChanged {
            binding.saveProfileButton.isEnabled = it.toString() != initialUsername
        }

        binding.saveProfileButton.setOnClickListener {
            binding.saveProfileButton.isEnabled = false
            binding.saveProfileButton.text = "Updating..." // show that something is happening
            val newName = binding.userName.text.toString()
            if (newName.isBlank()) {
                Toast.makeText(requireContext(), "Name cannot be blank", Toast.LENGTH_LONG).show()
            } else {
                viewModel.updateUserProfile(newName)
            }
        }

        binding.profilePictureOverlay.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    binding.profilePictureOverlay.visibility = View.VISIBLE
                }
                MotionEvent.ACTION_UP -> {
                    binding.profilePictureOverlay.visibility = View.INVISIBLE
                    checkPermissionForImage()
                }
            }
            true
        }

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.setText(it.name)
                initialUsername = it.name
                // Load image here
                // Glide.with(this).load(user.profileImageUrl).into(binding.profilePicture)
            }
        }

        // Error handling and user feedback
        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                // Show a Snackbar with a retry button
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") {
                        val newName = binding.userName.text.toString()
                        viewModel.updateUserProfile(newName)
                    }.show()
                viewModel.clearErrorMessage()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            if (successMessage != null) {
                // Toast.makeText(context, successMessage, Toast.LENGTH_LONG).show()
                // Instead of showing a toast, navigate back to the previous screen
                viewModel.clearSuccessMessage()
            }
        }
    }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val imageUri: Uri? = result.data?.data
            if(imageUri!=null) {
                viewModel.updateAvatar(imageUri)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        pickImageResultLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    AlertDialog.Builder(requireContext())
                        .setMessage("This permission is required to choose an avatar.")
                        .setPositiveButton("Grant") { _, _ ->
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().packageName, null)))
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                } else {
                    // Permission was granted, so you can continue with opening the image selection
                    pickImageFromGallery()
                }
            }
        }
    }

    private fun checkPermissionForImage() {
        if ((ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
        ) {
            // Permission denied, request it
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            // Permission granted and now check for internet connectivity
            if (NetworkUtil.isInternetAvailable(requireContext())) {
                // If connected, call pickImageFromGallery()
                pickImageFromGallery()
            } else {
                // If not connected, show a message
                Toast.makeText(requireContext(), "Please check your internet connection and try again", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }
}
