package com.krish.headsup.ui.viewholders

import android.view.View
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.krish.headsup.R
import com.krish.headsup.model.User
import com.krish.headsup.ui.components.CustomAvatarImageView
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp

class ProfileHeaderViewHolder(
    itemView: View,
    private val onMessageClick: () -> Unit,
    private val onFollowClick: () -> Unit,
    private val onUnfollowClick: () -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val authorAvatar: CustomAvatarImageView = itemView.findViewById(R.id.authorAvatar)
    private val followButton: MaterialButton = itemView.findViewById(R.id.followButton)
    private val followedButton: MaterialButton = itemView.findViewById(R.id.followedButton)
    private val sendMessageButton: ImageButton = itemView.findViewById(R.id.sendMessageButton)

    init {
        followButton.setOnClickListener {
            onFollowClick()
        }
        followedButton.setOnClickListener {
            onUnfollowClick()
        }
        sendMessageButton.setOnClickListener {
            onMessageClick()
        }
    }

    fun bind(otherUser: User, selfUser: User, isFollowing: Boolean) {
        GlideApp.with(authorAvatar.context)
            .load(otherUser.avatarUri)
            .signature(CustomCacheKeyGenerator(otherUser.avatarUri ?: ""))
            .placeholder(CustomAvatarImageView.defaultAvatar)
            .circleCrop()
            .into(authorAvatar)

        followButton.visibility = if (isFollowing) View.GONE else View.VISIBLE
        followedButton.visibility = if (isFollowing) View.VISIBLE else View.GONE
    }
}
