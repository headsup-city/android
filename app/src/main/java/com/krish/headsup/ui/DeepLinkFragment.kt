package com.krish.headsup.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.krish.headsup.R
import com.krish.headsup.viewmodel.DeepLinkViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeepLinkFragment : Fragment() {

    private val viewModel: DeepLinkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_deep_link, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        // Set click listener for the back button
        view.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            navController.navigateUp()
        }

        viewModel.navigationCommand.observe(viewLifecycleOwner) { command ->
            command?.let {
                navController.navigate(it)
                viewModel.onNavigationCommandHandled()
            }
        }

        activity?.intent?.let { intent ->
            if (intent.action == Intent.ACTION_VIEW) {
                val uri = intent.data
                if (uri != null) {
                    viewModel.handleDeepLink(uri)
                }
            }
        }
    }
}
