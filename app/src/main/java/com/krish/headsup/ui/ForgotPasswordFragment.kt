package com.krish.headsup.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import com.krish.headsup.viewmodel.ForgetPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {

    private val fpViewModel: ForgetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_password, container, false)

        val navController = NavHostFragment.findNavController(this)

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val recoverPasswordButton: Button = view.findViewById(R.id.recoverPasswordButton)
        val successMessageTextView: TextView = view.findViewById(R.id.successMessageTextView)
        val progressBar: ProgressBar = view.findViewById(R.id.loadingProgressBar)
        val backButton: ImageButton = view.findViewById(R.id.arrow_circle_button)

        fpViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                progressBar.visibility = View.VISIBLE
                recoverPasswordButton.visibility = View.GONE
                successMessageTextView.visibility = View.INVISIBLE
            } else {
                progressBar.visibility = View.GONE
                recoverPasswordButton.visibility = View.VISIBLE
            }
        }

        fpViewModel.isSuccessful.observe(viewLifecycleOwner) { isSuccessful ->
            if (isSuccessful) {
                hideKeyboard()
                emailEditText.text.clear()
                successMessageTextView.visibility = View.VISIBLE
                successMessageTextView.text = "Password recovery instructions sent."
            }
        }

        fpViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                successMessageTextView.visibility = View.INVISIBLE
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        recoverPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()
            if (email.isNotBlank()) {
                fpViewModel.forgotPassword(email)
            } else {
                Toast.makeText(context, "Please enter an email", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            navController.navigateUp()
        }

        view.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }

        return view
    }

    fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        // Check if no view has focus:
        val currentFocusedView = activity?.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}
