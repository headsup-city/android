package com.krish.headsup.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.krish.headsup.R
import com.krish.headsup.adapters.MessagingAdapter
import com.krish.headsup.databinding.FragmentMessagingBinding
import com.krish.headsup.viewmodel.MessagingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MessagingFragment : Fragment() {

    private val viewModel: MessagingViewModel by viewModels()
    private lateinit var binding: FragmentMessagingBinding
    private lateinit var adapter: MessagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagingBinding.inflate(inflater, container, false)

        // Retrieve convoId and userId from arguments
        val convoId = arguments?.getString("convoId")
        val userId = arguments?.getString("userId")

        adapter = MessagingAdapter()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString()
            if (text.isNotEmpty()) {
                if (convoId != null) {
                    viewModel.sendMessageToConversation(convoId, text)
                } else if (userId != null) {
                    viewModel.sendMessageToUser(userId, text)
                }
                binding.messageInput.text.clear()
            }
        }

        if (convoId != null) {
            viewModel.getMessages(convoId).asLiveData().observe(viewLifecycleOwner) { pagingData ->
                lifecycleScope.launch {
                    adapter.submitData(pagingData)
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = NavHostFragment.findNavController(this)

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        // Increase touchable area of the sendButton
        val sendButtonParent = binding.sendButton.parent as View

        sendButtonParent.post {
            val rect = Rect()
            binding.sendButton.getHitRect(rect)

            val extraPadding = resources.getDimensionPixelSize(R.dimen.size_32_button_inc)
            rect.top += extraPadding
            rect.left += extraPadding
            rect.right += extraPadding
            rect.bottom += extraPadding

            sendButtonParent.touchDelegate = TouchDelegate(rect, binding.sendButton)
        }

        // Increase touchable area of the backButton
        val backButtonParent = binding.backButton.parent as View

        backButtonParent.post {
            val rect = Rect()
            binding.backButton.getHitRect(rect)

            val extraPadding = resources.getDimensionPixelSize(R.dimen.size_32_button_inc)
            rect.top += extraPadding
            rect.left += extraPadding
            rect.right += extraPadding
            rect.bottom += extraPadding

            backButtonParent.touchDelegate = TouchDelegate(rect, binding.sendButton)
        }

        // Increase touchable area of the backButton
        val menuButtonParent = binding.menu.parent as View

        menuButtonParent.post {
            val rect = Rect()
            binding.menu.getHitRect(rect)

            val extraPadding = resources.getDimensionPixelSize(R.dimen.size_32_button_inc)
            rect.top += extraPadding
            rect.left += extraPadding
            rect.right += extraPadding
            rect.bottom += extraPadding

            menuButtonParent.touchDelegate = TouchDelegate(rect, binding.sendButton)
        }
    }

}
