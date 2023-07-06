package com.krish.headsup.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
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

    private var initialY = 0f
    private var finalY = 0f

    private var isDataObserverRegistered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagingBinding.inflate(inflater, container, false)

        // Retrieve convoId and userId from arguments
        val convoId = arguments?.getString("convoId")
        val userId = arguments?.getString("userId")


        sharedViewModel.user.observe(viewLifecycleOwner) { user ->
            adapter = MessagingAdapter(user?.id)
            binding.recyclerView.adapter = adapter
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = false
            reverseLayout = true
        }

        binding.sendButton.setOnClickListener {
            val text = binding.messageInput.text.toString().trim()
            if (binding.messageInput.isFocused) {
                if (text.isNotEmpty()) {
                    if (convoId != null) {
                        viewModel.sendMessageToConversation(convoId, text)
                    } else if (userId != null) {
                        viewModel.sendMessageToUser(userId, text)
                    }
                    binding.messageInput.text?.clear()
                }
            } else {
                binding.messageInput.requestFocus()
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(binding.messageInput, InputMethodManager.SHOW_IMPLICIT)
            }
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve convoId and userId from arguments
        val convoId = arguments?.getString("convoId")
        val userId = arguments?.getString("userId")

        // Initialize conversation data
        viewModel.initialize(userId, convoId)

        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            val chatItems = convertMessagesToChatItems(messages)
            adapter.submitList(chatItems) {
                binding.recyclerView.scrollToPosition(0)
            }
            viewModel.loadingStatus.value
            binding.encouragingMessage.visibility = View.GONE
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
            adapter.notifyDataSetChanged()
        }

        val navController = NavHostFragment.findNavController(this)

        binding.backButton.setOnClickListener {
            navController.navigateUp()
        }

        binding.recyclerView.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialY = motionEvent.y
                    hideKeyboard()
                    binding.messageInput.clearFocus()
                }
                MotionEvent.ACTION_UP -> {
                    finalY = motionEvent.y
                    if (finalY > initialY) {
                        // Scrolling up or a touch event
                        hideKeyboard()
                    }
                    // else we are scrolling down, do nothing
                }
            }
            false
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    showKeyboard(binding.messageInput)
                }
            }
        })

        binding.retryButton.setOnClickListener {
            viewModel.initialize(userId, convoId)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                MessagingViewModel.LoadingStatus.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                    binding.encouragingMessage.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                MessagingViewModel.LoadingStatus.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                    binding.recyclerView.visibility = View.VISIBLE
                    if (viewModel.messages.value.isNullOrEmpty()) {
                        binding.encouragingMessage.visibility = View.VISIBLE
                    } else {
                        binding.encouragingMessage.visibility = View.GONE
                    }
                }
                MessagingViewModel.LoadingStatus.Error -> {
                    binding.loadingIndicator.visibility = View.GONE
                    binding.encouragingMessage.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                    binding.retryButton.visibility = View.VISIBLE
                }
            }
        }

        binding.messageInputLayout.setOnClickListener {
            binding.messageInput.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.messageInput, InputMethodManager.SHOW_IMPLICIT)
        }

        setupSwipeDownListener()

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

        for (message in messages.asReversed()) {
            val messageDate = getRelativeTimeForChat(message.createdAt)

            if (currentDate == null || messageDate != currentDate) {
                chatItems.add(0, ChatItem.HeaderItem(messageDate))
                currentDate = messageDate
            }

            chatItems.add(0, ChatItem.MessageItem(message))
        }

        return chatItems
    }

    private fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputMethodManager = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        // Check if no view has focus:
        val currentFocusedView = activity?.currentFocus
        currentFocusedView?.let {
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            it.clearFocus()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSwipeDownListener() {
        val gestureDetector = GestureDetector(
            context,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onDown(e: MotionEvent): Boolean {
                    return true
                }

                override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
                    if (e1.y < e2.y) {
                        hideKeyboard()
                        return true
                    }
                    return false
                }
            }
        )

        binding.messageInputLayout.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized && !isDataObserverRegistered) {
            adapter.registerAdapterDataObserver(dataObserver)
            isDataObserverRegistered = true
        }
    }

    override fun onPause() {
        super.onPause()
        if (::adapter.isInitialized && isDataObserverRegistered) {
            adapter.unregisterAdapterDataObserver(dataObserver)
            isDataObserverRegistered = false
        }
    }
}
