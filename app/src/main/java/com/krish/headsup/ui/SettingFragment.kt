package com.krish.headsup.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.databinding.FragmentSettingBinding
import com.krish.headsup.managers.AuthManager
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserPreferences
import com.krish.headsup.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {

    @Inject
    lateinit var userPreferences: UserPreferences

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var authManager: AuthManager

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        binding.apply {
            backButton.setOnClickListener {
                navController.navigateUp()
            }

            termsConditionsButton.setOnClickListener {
                val action = SettingFragmentDirections.actionSettingFragmentToTermsFragment()
                navController.navigate(action)
            }

            privacyPolicyButton.setOnClickListener {
                // Perform action for Privacy Policy button
                val action = SettingFragmentDirections.actionSettingFragmentToPrivacyFragment()
                navController.navigate(action)
            }

            shareAppButton.setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "http://play.google.com/store/apps/details?id=com.krish.headsup")
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }

            logoutButton.setOnClickListener {
                authManager.updateAuthState(AuthState.NO_USER)
                userPreferences.deleteUser()
                tokenManager.clearTokens()
            }

            deleteAccountButton.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                    .setPositiveButton("Yes") { _, _ ->
                        val settingViewModel = viewModel
                        settingViewModel?.deleteUserAccount()
                    }
                    .setNegativeButton("No", null)
                    .show()
            }
        }
    }
}
