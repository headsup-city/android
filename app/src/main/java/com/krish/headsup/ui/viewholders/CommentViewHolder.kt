package com.krish.headsup.ui.viewholders

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.databinding.ItemCommentBinding
import com.krish.headsup.model.Comment
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(comment: Comment, requireContext: Context) {
        binding.apply {
            commentAuthor.text = comment.author?.name
            commentText.text = comment.text
            // Set your timestamp here
            commentTimestamp.text = getRelativeTime(comment.createdAt, requireContext) // Format your timestamp here

            // Load the author's avatar using Glide
            GlideApp.with(requireContext)
                .load(comment.author?.avatarUri)
                .signature(CustomCacheKeyGenerator(comment.author?.avatarUri ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(commentAvatar)
        }
    }
}
