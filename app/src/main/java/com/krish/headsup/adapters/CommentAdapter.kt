package com.krish.headsup.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.krish.headsup.databinding.ItemCommentBinding
import com.krish.headsup.model.Comment
import com.krish.headsup.ui.viewholders.CommentViewHolder

class CommentAdapter : PagingDataAdapter<Comment, CommentViewHolder>(CommentDiffCallback()) {
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        context = parent.context
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        if (comment != null) {
            holder.bind(comment, context)
            Log.d("TextPostFragment", "Comment at position $position: $comment")
        }
    }
    class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem == newItem
        }
    }
}
