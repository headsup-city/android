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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentEditProfileBinding
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
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

        binding.changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        var initialUsername: String? = null
        var initialPhoneNumber: String? = null

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.userName.doAfterTextChanged {
            val hasChanged = it.toString() != initialUsername || binding.phoneNumber.text.toString() != initialPhoneNumber
            binding.saveProfileButton.isEnabled = hasChanged
        }

        binding.phoneNumber.doAfterTextChanged {
            val hasChanged = it.toString() != initialPhoneNumber || binding.userName.text.toString() != initialUsername
            binding.saveProfileButton.isEnabled = hasChanged
        }

        binding.saveProfileButton.setOnClickListener {
            val newName = binding.userName.text.toString()
            val newPhoneNumber = binding.phoneNumber.text.toString()
            viewModel.updateUserProfile(newName, newPhoneNumber)
        }

        binding.profilePicture.setOnClickListener {
            checkPermissionForImage()
        }

        viewModel.initializeScreen(sharedViewModel.user.value)

        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.userName.setText(it.name)
                binding.phoneNumber.setText(it.mobileNumber)
                binding.emailLabel.text = it.email

                GlideApp.with(binding.profilePicture.context)
                    .load(it.avatarUri)
                    .signature(CustomCacheKeyGenerator(it.avatarUri ?: ""))
                    .placeholder(CustomAvatarImageView.defaultAvatar)
                    .circleCrop()
                    .into(binding.profilePicture)

                initialUsername = it.name
                initialPhoneNumber = it.mobileNumber
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (errorMessage != null) {
                Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry") {
                        val newName = binding.userName.text.toString()
                        val newPhoneNumber = binding.phoneNumber.text.toString()
                        viewModel.updateUserProfile(newName, newPhoneNumber)
                    }.show()
                viewModel.clearErrorMessage()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            if (successMessage != null) {
                viewModel.clearSuccessMessage()
            }
        }
    }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                viewModel.updateAvatar(imageUri)
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResultLauncher.launch(intent)
    }

    private fun checkPermissionForImage() {
        Log.d("EditProfileFragment", "Checking permission for image")

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            Log.d("EditProfileFragment", "Permission denied, requesting it")
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_CODE
            )
        } else {
            Log.d("EditProfileFragment", "Permission already granted, picking image")
            pickImageFromGallery()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted, pick image from gallery
                    pickImageFromGallery()
                } else {
                    // Permission denied, show an explanatory dialog or snackbar
                    AlertDialog.Builder(requireContext())
                        .setMessage("This permission is required to choose an avatar.")
                        .setPositiveButton("Grant") { _, _ ->
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().packageName, null)))
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val oldPassword = dialogView.findViewById<EditText>(R.id.oldPassword)
        val newPassword = dialogView.findViewById<EditText>(R.id.newPassword)
        val confirmPassword = dialogView.findViewById<EditText>(R.id.confirmPassword)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val oldPasswordText = oldPassword.text.toString()
                val newPasswordText = newPassword.text.toString()
                val confirmPasswordText = confirmPassword.text.toString()

                if (newPasswordText == confirmPasswordText) {
                    // Call ViewModel to change password
                    viewModel.changePassword(oldPasswordText, newPasswordText)
                } else {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.show()
    }


    companion object {
        private const val PERMISSION_CODE = 1001
    }
}
