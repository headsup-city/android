package com.krish.headsup.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentRegisterBinding
import com.krish.headsup.model.AuthState
import com.krish.headsup.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val registerViewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus()
        }

        binding.arrowCircleButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerUser()
                true
            } else {
                false
            }
        }

        binding.registerButton.setOnClickListener {
            hideKeyboardAndClearFocus()
            registerUser()
        }

        binding.registerNowClickableButton.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.termsButton.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_termsFragment)
        }

        binding.privacyPolicyButton.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_privacyFragment)
        }

        // Observe the ViewModel's authentication state
        registerViewModel.authState.observe(viewLifecycleOwner) { authState ->

            when (authState) {
                AuthState.LOADING -> {
                    // Show loading indicator
                }
                AuthState.AUTHENTICATED -> {
                    // Perform authenticated actions
                }
                else -> {
                    // Handle other states, such as error messages
                }
            }
        }
    }

    private fun registerUser() {
        val name = binding.nameEditText.text.toString()
        val email = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            binding.errorMessageTextView.visibility = View.VISIBLE
            binding.errorMessageTextView.text = getString(R.string.register_input_blank_message)
            return
        }

        // Call the ViewModel's registerUser method
        registerViewModel.registerUser(name, email, password)
    }

    private fun hideKeyboardAndClearFocus() {
        val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val focusedView = requireActivity().currentFocus

        focusedView?.let {
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            it.clearFocus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}
