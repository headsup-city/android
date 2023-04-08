package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
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
            navController.navigate(R.id.action_greetingsFragment_to_loginFragment)
        }

        binding.buttonLogin.setOnClickListener {
            // Get the NavController associated with the authNavigation graph
            val navController = NavHostFragment.findNavController(this)
            navController.navigate(R.id.action_greetingsFragment_to_loginFragment)
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
