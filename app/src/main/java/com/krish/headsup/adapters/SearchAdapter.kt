package com.krish.headsup.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.krish.headsup.databinding.ListSearchUserBinding
import com.krish.headsup.model.User
import com.krish.headsup.ui.search.SearchFragmentDirections
import com.krish.headsup.ui.search.SearchViewHolder

class SearchAdapter : ListAdapter<User, SearchViewHolder>(SearchDiffCallback()) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        context = parent.context
        val binding = ListSearchUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val result = getItem(position)
        if (result != null) {
            holder.bind(result, context)
        }
        holder.itemView.setOnClickListener {
            val action = SearchFragmentDirections.actionSearchFragmentToProfileFragment(result.id)
            holder.itemView.findNavController().navigate(action)
        }
    }

    class SearchDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
