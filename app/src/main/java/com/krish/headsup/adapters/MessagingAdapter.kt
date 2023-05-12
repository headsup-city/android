package com.krish.headsup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.krish.headsup.databinding.MessagingListBinding
import com.krish.headsup.model.Message
import com.krish.headsup.ui.ConversationFragmentDirections
import com.krish.headsup.ui.viewholders.MessagingViewHolder

class MessagingAdapter : PagingDataAdapter<Message, MessagingViewHolder>(
    MessageDiffCallback()
) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessagingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = MessagingListBinding.inflate(layoutInflater, parent, false)
        return MessagingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessagingViewHolder, position: Int) {
        val message = getItem(position)
        if (message != null) {
            holder.bind(message)
        }
        holder.itemView.setOnClickListener {
            if (message != null) {
                val action = ConversationFragmentDirections.actionConversationFragmentToMessagingFragment(message.id)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
