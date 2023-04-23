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
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.adapters.CommentAdapter
import com.krish.headsup.databinding.FragmentImagePostBinding
import com.krish.headsup.model.Post
import com.krish.headsup.viewmodel.ImagePostViewModel
import com.krish.headsup.viewmodel.ImagePostViewModel_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ImagePostFragment : Fragment() {

    @Inject
    lateinit var imagePostViewModelFactory: ImagePostViewModel_AssistedFactory
    private lateinit var binding: FragmentImagePostBinding
    private val commentAdapter = CommentAdapter()

    private val viewModel: ImagePostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ImagePostViewModel::class.java)) {
                    return imagePostViewModelFactory.create(SavedStateHandle(mapOf("post" to requireArguments().getParcelable<Post>("post")))) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.commentsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
    }

    private fun observeViewModel() {
        // Observe the post LiveData object
        viewModel.post.observe(viewLifecycleOwner) { post ->
            binding.apply {
                caption.text = post.caption
                // You can set other post details like author's avatar, post date, etc.
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.comments.collectLatest { pagingData ->
                commentAdapter.submitData(pagingData)
            }
        }

        commentAdapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        }
    }
}
