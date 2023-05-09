package com.krish.headsup.ui.search

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ListSearchUserBinding
import com.krish.headsup.model.User

class SearchViewHolder(private val binding: ListSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, requireContext: Context) {
        binding.apply {
        }
    }
}
