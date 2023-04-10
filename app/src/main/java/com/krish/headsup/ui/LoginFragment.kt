package com.krish.headsup.ui

import android.annotation.SuppressLint
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
import com.krish.headsup.databinding.FragmentLoginBinding
import com.krish.headsup.model.AuthState
import com.krish.headsup.utils.AuthStateChangeListener
import com.krish.headsup.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var authStateChangeListener: AuthStateChangeListener
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthStateChangeListener) {
            authStateChangeListener = context
        } else {
            throw RuntimeException("$context must implement AuthStateChangeListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        binding.root.setOnTouchListener { _, _ ->
            hideKeyboardAndClearFocus()
            true
        }

        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginUser()
                true
            } else {
                false
            }
        }



        binding.arrowCircleButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.forgotPasswordButton.setOnClickListener {
            // TODO: Navigate to Forgot Password Fragment
        }

        binding.loginButton.setOnClickListener {
            hideKeyboardAndClearFocus()
            loginUser()
        }

        binding.registerNowClickableButton.setOnClickListener {
            // TODO: Navigate to Register Fragment
        }

        binding.termsButton.setOnClickListener {
            // TODO: Navigate to Terms and Conditions Fragment
        }

        binding.privacyPolicyButton.setOnClickListener {
            // TODO: Navigate to Privacy Policy Fragment
        }

        // Observe the ViewModel's authentication state
        loginViewModel.authState.observe(viewLifecycleOwner) { authState ->
            authStateChangeListener.onAuthStateChanged(authState)

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

    private fun loginUser() {
        val emailOrPhone = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (emailOrPhone.isBlank() || password.isBlank()) {
            binding.errorMessageTextView.visibility = View.VISIBLE
            binding.errorMessageTextView.text = "Email/Phone and password cannot be empty."
            return
        }

        // Call the ViewModel's loginUser method instead
        loginViewModel.loginUser(emailOrPhone, password)
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
