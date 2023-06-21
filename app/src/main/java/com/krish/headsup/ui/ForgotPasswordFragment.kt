package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        val emailEditText: EditText = view.findViewById(R.id.emailEditText)
        val recoverPasswordButton: Button = view.findViewById(R.id.recoverPasswordButton)
        val successMessageTextView: TextView = view.findViewById(R.id.successMessageTextView)
        val progressBar = ProgressBar(view.context).apply { visibility = View.GONE } // Assume a ProgressBar for loading indicator

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

        return view
    }
}
