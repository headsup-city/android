package com.krish.headsup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ChatMessageDateHeaderBinding
import com.krish.headsup.databinding.MessagingListBinding
import com.krish.headsup.model.ChatItem
import com.krish.headsup.ui.viewholders.ChatDateViewHolder
import com.krish.headsup.ui.viewholders.MessagingViewHolder

class MessagingAdapter(private val selfUserId: String?) : ListAdapter<ChatItem, RecyclerView.ViewHolder>(
    ChatItemDiffCallback()
) {

    companion object {
        private const val VIEW_TYPE_MESSAGE_ITEM = 1
        private const val VIEW_TYPE_DATE_HEADER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChatItem.MessageItem -> VIEW_TYPE_MESSAGE_ITEM
            is ChatItem.HeaderItem -> VIEW_TYPE_DATE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_MESSAGE_ITEM -> {
                val binding = MessagingListBinding.inflate(layoutInflater, parent, false)
                MessagingViewHolder(binding, selfUserId)
            }

            VIEW_TYPE_DATE_HEADER -> {
                val binding = ChatMessageDateHeaderBinding.inflate(layoutInflater, parent, false)
                ChatDateViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChatItem.MessageItem -> (holder as MessagingViewHolder).bind(item.message)
            is ChatItem.HeaderItem -> (holder as ChatDateViewHolder).bind(item.date)
        }
    }

    class ChatItemDiffCallback : DiffUtil.ItemCallback<ChatItem>() {
        override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }

            return when (oldItem) {
                is ChatItem.MessageItem -> oldItem.message.id == (newItem as ChatItem.MessageItem).message.id
                is ChatItem.HeaderItem -> oldItem.date == (newItem as ChatItem.HeaderItem).date
            }
        }

        override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
            return oldItem == newItem
        }
    }
}
