package com.krish.headsup.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.adapters.ConversationAdapter
import com.krish.headsup.databinding.FragmentConversationBinding
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.viewmodel.ConversationViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ConversationFragment : Fragment() {
    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: ConversationViewModel by viewModels()
    private lateinit var adapter: ConversationAdapter

    @Inject
    lateinit var tokenManager: TokenManager

    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ConversationAdapter(sharedViewModel, tokenManager, userRepository)
        val navController = NavHostFragment.findNavController(this)

        binding.conversationListItem.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ConversationFragment.adapter
        }

        viewModel.getConversations()

        viewModel.conversations?.let { pagingDataFlow ->
            lifecycleScope.launch {
                pagingDataFlow.collectLatest { pagingData ->
                    adapter.submitData(pagingData)
                }
            }
        }

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                when (val refreshState = loadStates.refresh) {
                    is LoadState.Loading -> {
                        binding.loading.visibility = View.VISIBLE
                        binding.errorMessage.visibility = View.GONE
                        binding.emptyState.visibility = View.GONE
                    }
                    is LoadState.Error -> {
                        binding.loading.visibility = View.GONE
                        binding.errorMessage.visibility = View.VISIBLE
                        binding.errorMessage.text = getString(R.string.error_loading_data)
                    }
                    is LoadState.NotLoading -> {
                        binding.loading.visibility = View.GONE
                        if (loadStates.append.endOfPaginationReached && adapter.itemCount < 1) {
                            binding.emptyState.visibility = View.VISIBLE
                        } else {
                            binding.emptyState.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
