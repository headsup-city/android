package com.krish.headsup.ui.viewholders

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ConversationListBinding
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class ConversationViewHolder(private val binding: ConversationListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(conversation: ConversationFull) {
        val context = itemView.context
        Log.d("DebugSelf", "CVH15")
        binding.apply {
            nameTextView.text = conversation.lastMessageAuthor?.name
            lastMessageTextView.text = conversation.lastMessage?.text
            timeTextView.text = getRelativeTime(conversation.updatedAt, context)

            GlideApp.with(context)
                .load(conversation.image)
                .signature(CustomCacheKeyGenerator(conversation.image ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(avatarImageView)
        }
    }
}
