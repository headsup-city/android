package com.krish.headsup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.model.User
import com.krish.headsup.ui.viewholders.ProfileHeaderViewHolder
import com.krish.headsup.viewmodel.SharedViewModel

class ProfileHeaderAdapter(
    private val onMessageClick: () -> Unit,
    private val onFollowClick: () -> Unit,
    private val onUnfollowClick: () -> Unit,
    private val onEditProfileClick: () -> Unit,
    private val sharedViewModel: SharedViewModel,
) :
    RecyclerView.Adapter<ProfileHeaderViewHolder>() {

    private var otherUser: User? = null
    private var isFollowingOtherUser: Boolean = false

    fun setData(otherUser: User, isFollowingOtherUser: Boolean) {
        this.otherUser = otherUser
        this.isFollowingOtherUser = isFollowingOtherUser
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_profile, parent, false)
        return ProfileHeaderViewHolder(view, onMessageClick, onFollowClick, onUnfollowClick, onEditProfileClick)
    }

    override fun onBindViewHolder(holder: ProfileHeaderViewHolder, position: Int) {
        otherUser?.let { otherUser ->
            sharedViewModel.user.value?.let { currentUser ->
                holder.bind(otherUser, currentUser, isFollowingOtherUser)
            }
        }
    }

    override fun getItemCount(): Int = if (otherUser != null) 1 else 0
}
