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
import com.krish.headsup.databinding.FragmentTextPostBinding
import com.krish.headsup.model.Post
import com.krish.headsup.viewmodel.ImagePostViewModel
import com.krish.headsup.viewmodel.ImagePostViewModel_AssistedFactory
import com.krish.headsup.viewmodel.TextPostViewModel
import com.krish.headsup.viewmodel.TextPostViewModel_AssistedFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TextPostFragment : Fragment() {
    @Inject
    lateinit var textPostViewModelFactory: TextPostViewModel_AssistedFactory
    private lateinit var binding: FragmentTextPostBinding
    private val commentAdapter = CommentAdapter()

    private val viewModel: TextPostViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(TextPostViewModel::class.java)) {
                    return textPostViewModelFactory.create(SavedStateHandle(mapOf("post" to requireArguments().getParcelable<Post>("post")))) as T
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
        binding = FragmentTextPostBinding.inflate(inflater, container, false)
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
                postTextContent.text = post.caption
                authorName.text = post.author?.name
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
