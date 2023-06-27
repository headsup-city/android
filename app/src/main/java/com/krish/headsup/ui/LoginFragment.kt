package com.krish.headsup.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentLoginBinding
import com.krish.headsup.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        binding.root.setOnClickListener {
            hideKeyboardAndClearFocus()
        }

        binding.passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginUser()
                true
            } else {
                false
            }
        }

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.forgotPasswordButton.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            navController.navigate(action)
        }

        binding.loginButton.setOnClickListener {
            hideKeyboardAndClearFocus()
            loginUser()
        }

        binding.registerNowClickableButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.termsButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_termsFragment)
        }

        binding.privacyPolicyButton.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_privacyFragment)
        }

        loginViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loginButton.visibility = if (isLoading)View.GONE else View.VISIBLE
            binding.loadingProgressBar.visibility = if (isLoading)View.VISIBLE else View.GONE
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser() {
        val emailOrPhone = binding.usernameEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (emailOrPhone.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Email/Phone and password cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

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
