package com.krish.headsup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.krish.headsup.databinding.ConversationListBinding
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.ui.viewholders.ConversationViewHolder

class ConversationAdapter : PagingDataAdapter<ConversationFull, ConversationViewHolder>(
    ConversationDiffCallback()
) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ConversationListBinding.inflate(layoutInflater, parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversationFull = getItem(position)
        if (conversationFull != null) {
            holder.bind(conversationFull)
        }
    }

    class ConversationDiffCallback : DiffUtil.ItemCallback<ConversationFull>() {
        override fun areItemsTheSame(oldItem: ConversationFull, newItem: ConversationFull): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ConversationFull, newItem: ConversationFull): Boolean {
            return oldItem == newItem
        }
    }
}
