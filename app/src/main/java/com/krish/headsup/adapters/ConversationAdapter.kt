package com.krish.headsup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.krish.headsup.databinding.ConversationListBinding
import com.krish.headsup.model.ConversationFull
import com.krish.headsup.repositories.UserRepository
import com.krish.headsup.ui.ConversationFragmentDirections
import com.krish.headsup.ui.viewholders.ConversationViewHolder
import com.krish.headsup.utils.TokenManager
import com.krish.headsup.viewmodel.SharedViewModel

class ConversationAdapter(
    private val sharedViewModel: SharedViewModel,
    private val tokenManager: TokenManager,
    private val userRepository: UserRepository
) : PagingDataAdapter<ConversationFull, ConversationViewHolder>(
    ConversationDiffCallback()
) {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ConversationListBinding.inflate(layoutInflater, parent, false)
        return ConversationViewHolder(binding, sharedViewModel, tokenManager, userRepository)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversationFull = getItem(position)
        if (conversationFull != null) {
            holder.bind(conversationFull)
        }
        holder.itemView.setOnClickListener {
            if (conversationFull != null) {
                val action = ConversationFragmentDirections.actionConversationFragmentToMessagingFragment(conversationFull.id, null)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    override fun onViewRecycled(holder: ConversationViewHolder) {
        super.onViewRecycled(holder)
        holder.clear()
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
