package com.krish.headsup.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.krish.headsup.R
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

        val rootView = view.rootView
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom

            val isKeyboardOpen = keypadHeight > screenHeight * 0.15
            val bottomNavigation = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNavigation?.visibility = if (isKeyboardOpen) View.GONE else View.VISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility", "InternalInsetResource")
    private fun setupViews() {
        val statusBarHeight: Int
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        statusBarHeight = if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else {
            // Convert 24dp to pixels as default status bar height if resource id not found
            (24 * resources.displayMetrics.density).toInt()
        }
        binding.constraintLayout.setPadding(0, statusBarHeight, 0, 0)

        binding.coordinatorLayout.setOnClickListener {
            if (binding.searchInput.isFocused) {
                hideKeyboardAndClearFocus()
            }
        }

        binding.constraintLayout.setOnTouchListener { v, event ->
            hideKeyboardAndClearFocus()
            return@setOnTouchListener false
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchInput.text.toString()
                if (!query.isNullOrEmpty()) {
                    viewModel.onQueryChanged(query)
                    true
                } else {
                    false
                }
            } else {
                false
            }
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.searchHintText.visibility = View.VISIBLE
                    binding.noUserFoundText.visibility = View.GONE
                    binding.closeIcon.visibility = View.GONE
                    binding.searchIcon.visibility = View.VISIBLE
                    searchAdapter.submitList(emptyList()) // Clear the list
                } else {
                    binding.searchHintText.visibility = View.GONE
                    binding.closeIcon.visibility = View.VISIBLE
                    binding.searchIcon.visibility = View.GONE
                    viewModel.onQueryChanged(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.noUserFoundText.setOnClickListener { hideKeyboardAndClearFocus() }
        binding.searchHintText.setOnClickListener { hideKeyboardAndClearFocus() }
        binding.userList.setOnTouchListener { _, _ -> hideKeyboardAndClearFocus(); false }

        binding.closeIcon.setOnClickListener {
            binding.searchInput.setText("")
            binding.closeIcon.visibility = View.GONE
            searchAdapter.submitList(emptyList()) // Clear the list
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.searchResults.collect { userList ->
                    if (userList.isEmpty()) {
                        if (binding.searchInput.text?.isNotEmpty() == true) {
                            binding.noUserFoundText.visibility = View.VISIBLE
                            binding.searchHintText.visibility = View.GONE
                        } else {
                            binding.noUserFoundText.visibility = View.GONE
                            binding.searchHintText.visibility = View.VISIBLE
                        }
                        binding.userList.visibility = View.GONE
                    } else {
                        binding.noUserFoundText.visibility = View.GONE
                        binding.searchHintText.visibility = View.GONE
                        binding.userList.visibility = View.VISIBLE
                        searchAdapter.submitList(userList)
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun hideKeyboardAndClearFocus() {
        binding.searchInput.clearFocus()
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchInput.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
