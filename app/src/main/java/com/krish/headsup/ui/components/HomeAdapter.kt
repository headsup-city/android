package com.krish.headsup.ui.components

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.krish.headsup.R
import com.krish.headsup.model.Post

class HomeAdapter(private val onPostClickListener: OnPostClickListener) :
    PagingDataAdapter<Post, PostView>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostView {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_post, parent, false)
        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        return PostView(view, screenWidth)
    }

    override fun onBindViewHolder(holder: PostView, position: Int) {
        val post = getItem(position)
        holder.bind(post)

        // Set OnClickListener for the post item
        holder.itemView.setOnClickListener {
            post?.let { postItem ->
                onPostClickListener.onPostClick(postItem, holder.itemView.id)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostView) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetachedFromWindow()
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

    interface OnPostClickListener {
        fun onPostClick(post: Post, navHostViewId: Int)
    }
}
