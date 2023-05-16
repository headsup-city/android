package com.krish.headsup.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ChatMessageDateHeaderBinding

class ChatDateViewHolder(private val binding: ChatMessageDateHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(date: String) {
        binding.dateHeader.text = date
    }
}
