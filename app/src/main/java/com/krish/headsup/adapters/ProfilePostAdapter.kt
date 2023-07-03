package com.krish.headsup.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.model.Post
import com.krish.headsup.ui.components.PostView
import com.krish.headsup.viewmodel.SharedViewModel

class ProfilePostAdapter(
    private val onCommentClickListener: PostView.OnCommentClickListener,
    private val onAuthorClickListener: PostView.OnAuthorClickListener,
    private val likeButtonClickListener: PostView.OnLikeButtonClickListener,
    private val postMenuActionListener: PostView.PostMenuActionListener,
    private val lifecycleOwner: LifecycleOwner,
    private val sharedViewModel: SharedViewModel,
) :
    PagingDataAdapter<Post, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_post, parent, false)
        val screenWidth = parent.context.resources.displayMetrics.widthPixels
        return PostView(view, screenWidth, onCommentClickListener, onAuthorClickListener, likeButtonClickListener, postMenuActionListener, lifecycleOwner, sharedViewModel)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PostView) {
            val post = getItem(position)
            post?.let { holder.bind(it) }
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder is PostView) {
            holder.onDetachedFromWindow()
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is PostView) {
            holder.customVideoPlayer.resetPlayer()
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }
}
