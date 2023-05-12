package com.krish.headsup.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.MessagingListBinding
import com.krish.headsup.model.Message

class MessagingViewHolder(private val binding: MessagingListBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(conversation: Message) {
        val context = itemView.context
        binding.apply {
        }
    }
}
