package com.krish.headsup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.ui.viewholders.SelfProfileHeaderViewHolder
import com.krish.headsup.viewmodel.SharedViewModel

class SelfProfileHeaderAdapter(
    private val onEditProfileClick: () -> Unit,
    private val onSettingButtonClick: () -> Unit,
    private val sharedViewModel: SharedViewModel,
) :
    RecyclerView.Adapter<SelfProfileHeaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelfProfileHeaderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.header_self_profile, parent, false)
        return SelfProfileHeaderViewHolder(view, onEditProfileClick, onSettingButtonClick)
    }

    override fun onBindViewHolder(holder: SelfProfileHeaderViewHolder, position: Int) {
        sharedViewModel.user.value?.let { currentUser ->
            holder.bind(currentUser)
        }
    }

    override fun getItemCount(): Int = 1
}
