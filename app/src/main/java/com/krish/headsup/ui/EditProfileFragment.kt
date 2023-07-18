package com.krish.headsup.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentEditProfileBinding
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.EditProfileViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    @Inject
    lateinit var tokenManager: TokenManager

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

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        var initialUsername: String? = null
        var initialPhoneNumber: String? = null

        GlideApp.with(binding.profilePicture.context)
            .load(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(binding.profilePicture)

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

        viewModel.notifyProfileUpdate.observe(viewLifecycleOwner) { isUpdated ->
            if (isUpdated) {
                val accessToken = tokenManager.getTokenStore()?.access?.token
                if (!accessToken.isNullOrEmpty()) {
                    sharedViewModel.fetchUserData(accessToken)
                    viewModel.resetNotifyProfileUpdate()
                }
            }
        }

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
                val toast = Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG)
                toast.show()
                Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 5000)
                viewModel.clearErrorMessage()
            }
        }

        viewModel.successMessage.observe(viewLifecycleOwner) { successMessage ->
            if (successMessage != null) {
                val toast = Toast.makeText(requireContext(), successMessage, Toast.LENGTH_LONG)
                toast.show()
                Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 5000)
                val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
                var view = activity?.currentFocus
                if (view == null) {
                    view = View(activity)
                }
                imm?.hideSoftInputFromWindow(view.windowToken, 0)
                viewModel.clearSuccessMessage()
            }
        }
    }

    private val pickImageResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            if (imageUri != null) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Do you want to upload this image?")
                    .setPositiveButton("Yes") { _, _ ->
                        GlideApp.with(binding.profilePicture.context)
                            .load(imageUri)
                            .circleCrop()
                            .into(binding.profilePicture)
                        viewModel.updateAvatar(imageUri)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageResultLauncher.launch(intent)
    }

    private fun checkPermissionForImage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
            } else {
                pickImageFromGallery()
            }
        } else {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            } else {
                pickImageFromGallery()
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val isGranted = permissions.entries.all { it.value }
            if (isGranted) {
                // Permission has been granted, continue with picking image
                pickImageFromGallery()
            } else {
                AlertDialog.Builder(requireContext())
                    .setMessage("This permission is required to choose an avatar.")
                    .setPositiveButton("Grant") { _, _ ->
                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", requireContext().packageName, null)))
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
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
                    val toast = Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_LONG)
                    toast.show()

                    // Dismiss the toast after 1 second
                    Handler(Looper.getMainLooper()).postDelayed({ toast.cancel() }, 1000)
                }
            }
            .setNegativeButton("Cancel", null)
            .setOnDismissListener {
                hideKeyboard()
            }
            .create()
        dialog.show()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    companion object {
        private const val PERMISSION_CODE = 1001
    }
}
