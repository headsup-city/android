package com.krish.headsup.views.components


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ConversationItemBinding

class ConversationItemViewHolder(private val binding: ConversationItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(conversation: Conversation) {
        // Bind the conversation data to the UI elements in conversation_item.xml
        binding.userNameTextView.text = conversation.userName
        binding.lastMessageTextView.text = conversation.lastMessage
        // Load the user's profile picture using a library like Glide or Picasso
    }

    companion object {
        fun create(parent: ViewGroup): ConversationItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ConversationItemBinding.inflate(inflater, parent, false)
            return ConversationItemViewHolder(binding)
        }
    }
}

