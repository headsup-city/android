package com.krish.headsup.ui.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ItemCommentBinding
import com.krish.headsup.model.Comment

class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment) {
        binding.apply {
            commentAuthor.text = comment.author?.name
            commentText.text = comment.text
            // You can set other comment details like author's avatar, comment date, etc.
        }
    }
}
