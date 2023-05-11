package com.krish.headsup.ui.search

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.databinding.ListSearchUserBinding
import com.krish.headsup.model.User
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class SearchViewHolder(private val binding: ListSearchUserBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User, context: Context) {
        binding.apply {
            // Set the user's name
            nameText.text = user.name

            // Load the user's avatar using Glide
            if (CustomAvatarImageView.defaultAvatar == null) {
                CustomAvatarImageView.defaultAvatar = ContextCompat.getDrawable(context, R.drawable.default_avatar)
            }
            GlideApp.with(context)
                .load(user.avatarUri)
                .signature(CustomCacheKeyGenerator(user.avatarUri ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(commentAvatar)
        }
    }
}
