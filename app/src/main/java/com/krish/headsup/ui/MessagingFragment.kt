package com.krish.headsup.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.adapters.MessagingAdapter
import com.krish.headsup.databinding.FragmentMessagingBinding
import com.krish.headsup.model.ChatItem
import com.krish.headsup.model.Message
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.getRelativeTimeForChat
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.MessagingViewModel
import com.krish.headsup.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MessagingFragment : Fragment() {

    private val viewModel: MessagingViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
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

        adapter = MessagingAdapter(sharedViewModel.user.value?.id)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = false
            reverseLayout = true
        }

        binding.recyclerView.adapter = adapter

        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString().trim()
            if (text.isNotEmpty()) {
                if (convoId != null) {
                    viewModel.sendMessageToConversation(convoId, text)
                } else if (userId != null) {
                    viewModel.sendMessageToUser(userId, text)
                }
                binding.messageInput.text.clear()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve convoId and userId from arguments
        val convoId = arguments?.getString("convoId")
        val userId = arguments?.getString("userId")

        // Initialize conversation data
        viewModel.initialize(userId, convoId)

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val chatItems = convertMessagesToChatItems(messages)
            adapter.submitList(chatItems)
        }

        // Observe the otherUser results
        viewModel.otherUser.observe(viewLifecycleOwner) { otherUser ->

            // Update the conversation title with the other user's name
            binding.conversationTitle.text = otherUser.name

            // Update the avatar with the other user's avatar URL using Glide
            if (CustomAvatarImageView.defaultAvatar == null) {
                CustomAvatarImageView.defaultAvatar = context?.let { ContextCompat.getDrawable(it, R.drawable.default_avatar) }
            }

            GlideApp.with(requireContext())
                .load(otherUser.avatarUri)
                .signature(CustomCacheKeyGenerator(otherUser.avatarUri ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(binding.conversationAvater)
        }

        sharedViewModel.user.observe(viewLifecycleOwner) { user ->
            viewModel.updateUser(user)
        }

        val navController = NavHostFragment.findNavController(this)

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        // Increase touchable area of the sendButton
        increaseTouchableArea(binding.sendButton, R.dimen.size_32_button_inc)

        // Increase touchable area of the backButton
        increaseTouchableArea(binding.backButton, R.dimen.size_32_button_inc)

        // Increase touchable area of the menu button
        increaseTouchableArea(binding.menu, R.dimen.size_32_button_inc)
    }

    private fun increaseTouchableArea(view: View, extraPaddingRes: Int) {
        (view.parent as View).post {
            val rect = Rect()
            view.getHitRect(rect)

            val extraPadding = resources.getDimensionPixelSize(extraPaddingRes)
            rect.top -= extraPadding
            rect.left -= extraPadding
            rect.right += extraPadding
            rect.bottom += extraPadding

            (view.parent as View).touchDelegate = TouchDelegate(rect, view)
        }
    }

    private val dataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            val totalItemCount = adapter.itemCount
            if (positionStart == totalItemCount - 1) {
                binding.recyclerView.scrollToPosition(positionStart)
            }
        }
    }

    private fun convertMessagesToChatItems(messages: List<Message>): List<ChatItem> {
        val chatItems = mutableListOf<ChatItem>()
        var currentDate: String? = null

        for (message in messages) {
            val messageDate = getRelativeTimeForChat(message.createdAt)

            chatItems.add(ChatItem.MessageItem(message))

            if (currentDate == null || messageDate != currentDate) {
                chatItems.add(ChatItem.HeaderItem(messageDate))
                currentDate = messageDate
            }
        }

        return chatItems
    }

    override fun onResume() {
        super.onResume()
        adapter.registerAdapterDataObserver(dataObserver)
    }

    override fun onPause() {
        super.onPause()
        adapter.unregisterAdapterDataObserver(dataObserver)
    }
}
