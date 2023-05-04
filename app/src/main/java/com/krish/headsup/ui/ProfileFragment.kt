package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.databinding.FragmentProfileBinding
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.PostPagingDataAdapter
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.viewmodel.ProfileViewModel
import com.krish.headsup.viewmodel.ProfileViewModel_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment(), PostPagingDataAdapter.OnPostClickListener, PostView.OnAuthorClickListener {

    @Inject
    lateinit var profileViewModelFactory: ProfileViewModel_AssistedFactory

    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                    return profileViewModelFactory.create(
                        SavedStateHandle(
                            mapOf(
                                "userId" to (
                                    arguments?.getString("userId")
                                        ?: ""
                                    )
                            )
                        )
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostPagingDataAdapter(this, this)
        binding.profileRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.profileRecyclerview.adapter = adapter

        viewModel.posts.observe(viewLifecycleOwner) { pagingDataFlow ->
            viewModel.viewModelScope.launch {
                pagingDataFlow?.collectLatest { pagingData ->
                    // Update the adapter's data with the new posts and notify the adapter
                    adapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onPostClick(post: Post, navHostViewId: Int) {
        // Implement the logic for handling post clicks here
    }

    override fun onAuthorClick(userId: String) {
        // Implement the logic for handling post clicks here
    }
}
