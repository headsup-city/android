package com.krish.headsup.ui.viewholders

import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.model.User
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class SelfProfileHeaderViewHolder(
    itemView: View,
    private val onEditProfileClick: () -> Unit,
    private val onSettingButtonClick: () -> Unit,
) : RecyclerView.ViewHolder(itemView) {
    private val authorAvatar: CustomAvatarImageView = itemView.findViewById(R.id.authorAvatar)
    private val editProfileButton: ImageButton = itemView.findViewById(R.id.editProfileButton)
    private val settingButton: ImageButton = itemView.findViewById(R.id.settingButton)
    private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

    init {
        editProfileButton.setOnClickListener {
            onEditProfileClick()
        }

        settingButton.setOnClickListener {
            onSettingButtonClick()
        }
    }

    fun bind(user: User) {
        // Initial placeholder for avatar image
        GlideApp.with(authorAvatar.context)
            .load(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(authorAvatar)

        GlideApp.with(authorAvatar.context)
            .load(user.avatarUri)
            .signature(CustomCacheKeyGenerator(user.avatarUri ?: ""))
            .placeholder(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(authorAvatar)

        nameTextView.text = user.name
    }
}
