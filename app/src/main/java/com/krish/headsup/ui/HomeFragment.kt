package com.krish.headsup.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.databinding.FragmentHomeBinding
import com.krish.headsup.ui.components.HomeAdapter
import com.krish.headsup.utils.Resource
import com.krish.headsup.viewmodel.HomeViewModel
import androidx.fragment.app.activityViewModels
import com.krish.headsup.R
import com.krish.headsup.utils.makeStatusBarTranslucent
import com.krish.headsup.utils.restoreStatusBar

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by activityViewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = HomeAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        val toolbarTitle = view.findViewById<TextView>(R.id.toolbarTitleText)
        val toolbarTitleIcon = view.findViewById<ImageView>(R.id.toolbarTitleIcon)
        val convoButton = view.findViewById<ImageView>(R.id.toolbarRightButton)

        toolbarTitle.text = getString(R.string.near_you)
        toolbarTitleIcon.visibility=View.VISIBLE

        // Set an OnClickListener for the optional convo button
        convoButton.visibility=View.VISIBLE
        convoButton.setOnClickListener {
            // Handle button click
        }

        viewModel.posts.observe(viewLifecycleOwner) { resource ->
            when (resource.status) {
                Resource.Status.SUCCESS -> {
                    adapter.submitList(resource.data)
                }
                Resource.Status.ERROR -> {
                    // Handle the error case, e.g., show a toast or a message to the user
                }
                Resource.Status.LOADING -> {
                    // Handle the loading case, e.g., show a loading spinner
                }
            }
        }

        viewModel.loadPosts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
