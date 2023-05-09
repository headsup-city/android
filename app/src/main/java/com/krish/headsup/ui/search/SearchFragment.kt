package com.krish.headsup.ui.search

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.adapters.SearchAdapter
import com.krish.headsup.databinding.FragmentSearchBinding
import com.krish.headsup.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private val searchAdapter = SearchAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userList.adapter = searchAdapter
        binding.userList.layoutManager = LinearLayoutManager(context)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {

        binding.coordinatorLayout.setOnClickListener {
            if (binding.searchInput.isFocused) {
                hideKeyboardAndClearFocus()
            }
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString()
                viewModel.onQueryChanged(query)
                true
            } else {
                false
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.closeIcon.visibility = View.GONE
                    binding.searchIcon.visibility = View.VISIBLE
                } else {
                    binding.closeIcon.visibility = View.VISIBLE
                    binding.searchIcon.visibility = View.GONE
                }
                viewModel.onQueryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.closeIcon.setOnClickListener {
            binding.searchInput.setText("")
            binding.closeIcon.visibility = View.GONE
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.searchResults.collect { userList ->
                    searchAdapter.submitList(userList)
                }
            } catch (e: Exception) {
                // Handle the error here
                Log.e("Debug", "Error occurred while collecting: ${e.localizedMessage}")
            }
        }
    }

    private fun hideKeyboardAndClearFocus() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
        binding.searchInput.clearFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
