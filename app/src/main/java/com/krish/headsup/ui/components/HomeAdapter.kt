package com.krish.headsup.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.model.Post

class HomeAdapter : ListAdapter<Post, HomeAdapter.HomeViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val postView = LayoutInflater.from(parent.context).inflate(com.krish.headsup.R.layout.post_view, parent, false) as PostView
        return HomeViewHolder(postView)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val post = getItem(position)
        holder.postView.bind(post)
    }

    class HomeViewHolder(val postView: PostView) : RecyclerView.ViewHolder(postView)

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
