package com.krish.headsup.ui

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.SignInButton
import com.krish.headsup.R
import com.krish.headsup.databinding.FragmentGreetingsBinding
import makeStatusBarTranslucent
import restoreStatusBar

class GreetingsFragment : Fragment() {

    private var _binding: FragmentGreetingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGreetingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            // Get the NavController associated with the authNavigation graph
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_greetingsFragment_to_registerFragment)
        }

        binding.buttonLogin.setOnClickListener {
            // Get the NavController associated with the authNavigation graph
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_greetingsFragment_to_loginFragment)
        }

        // Customize the SignInButton
        val signInButton = binding.buttonGoogleSignIn
        signInButton.setSize(SignInButton.SIZE_WIDE)
        signInButton.setColorScheme(SignInButton.COLOR_DARK)

        for (i in 0 until signInButton.childCount) {
            val childView = signInButton.getChildAt(i)
            if (childView is TextView) {
                childView.text = getString(R.string.sign_in_with_google)
                childView.textSize = 18f
                childView.setTextColor(Color.WHITE)
                childView.setTypeface(Typeface.DEFAULT_BOLD)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        makeStatusBarTranslucent()
    }

    override fun onPause() {
        super.onPause()
        restoreStatusBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
