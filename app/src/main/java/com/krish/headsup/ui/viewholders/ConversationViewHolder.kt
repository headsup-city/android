package com.krish.headsup.ui.viewholders

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.databinding.ConversationListBinding
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.utils.UserResult
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationViewHolder(
    private val binding: ConversationListBinding,
    private val sharedViewModel: SharedViewModel,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository,
) : RecyclerView.ViewHolder(binding.root) {
    private var job: Job? = null

    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
    private val lastMessageTextView: TextView = itemView.findViewById(R.id.lastMessageTextView)
    private val timeTextView: TextView = itemView.findViewById(R.id.timeTextView)
    private val avatarImageView: CustomAvatarImageView = itemView.findViewById(R.id.avatarImageView)

    fun bind(conversation: ConversationFull) {
        val context = itemView.context
        val otherUserId = conversation.participants.first { it != sharedViewModel.user.value?.id }

        // Initial placeholder for avatar image
        GlideApp.with(context)
            .load(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(avatarImageView)

        job?.cancel()
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val token = tokenManager.getTokenStore()?.access?.token
                val result = userRepository.getUser("Bearer $token", otherUserId)
                if (result is UserResult) {
                    withContext(Dispatchers.Main) {
                        // Update UI on main thread
                        nameTextView.text = result.data.name
                        GlideApp.with(context)
                            .load(result.data.avatarUri) // Use avatar URL of the user
                            .signature(CustomCacheKeyGenerator(result.data.avatarUri ?: ""))
                            .placeholder(CustomAvatarImageView.defaultAvatar)
                            .circleCrop()
                            .into(avatarImageView)
                    }
                }
            } catch (e: Exception) {
                // handle error
            }
        }

        lastMessageTextView.text = conversation.lastMessage?.text
        timeTextView.text = getRelativeTime(conversation.updatedAt)
    }

    fun clear() {
        job?.cancel()
    }
}
