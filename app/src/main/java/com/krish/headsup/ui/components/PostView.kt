package com.krish.headsup.ui.components

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.model.Post
import com.krish.headsup.model.PostState
import com.krish.headsup.utils.ViewUtils
import com.krish.headsup.utils.dpToPx
import com.krish.headsup.utils.getRelativeTime
import com.krish.headsup.utils.glide.CustomCacheKeyGenerator
import com.krish.headsup.utils.glide.GlideApp
import com.krish.headsup.viewmodel.SharedViewModel

class PostView(
    itemView: View,
    private val screenWidth: Int,
    private val onCommentClickListener: OnCommentClickListener,
    private val onAuthorClickListener: OnAuthorClickListener,
    private val likeButtonClickListener: OnLikeButtonClickListener,
    private val postMenuActionListener: PostMenuActionListener,
    private val lifecycleOwner: LifecycleOwner,
    private val sharedViewModel: SharedViewModel
) : RecyclerView.ViewHolder(itemView) {
    private val context: Context = itemView.context
    private var mutablePost: Post? = null
    private val viewUtils = ViewUtils()

    private val postBody: LinearLayout = itemView.findViewById(R.id.postBody)
    private val postHeader: LinearLayout = itemView.findViewById(R.id.postHeader)
    private val authorAvatar: CustomAvatarImageView = itemView.findViewById(R.id.authorAvatar)
    private val authorName: TextView = itemView.findViewById(R.id.authorName)
    private val postDate: TextView = itemView.findViewById(R.id.postDate)
    private val postImage: ImageView = itemView.findViewById(R.id.postImage)
    private val postText: TextView = itemView.findViewById(R.id.postText)
    val customVideoPlayer: CustomVideoPlayer = itemView.findViewById(R.id.customVideoPlayer)
    private val likeButton: ImageView = itemView.findViewById(R.id.likeButton)
    private val alreadyLikedButton: ImageView = itemView.findViewById(R.id.alreadyLikedButton)
    private val likeCountText: TextView = itemView.findViewById(R.id.likeCount)
    private val commentButton: ImageView = itemView.findViewById(R.id.commentButton)
    private val commentCountText: TextView = itemView.findViewById(R.id.commentCount)
    private val shareButton: ImageView = itemView.findViewById(R.id.shareButton)
    private val menuButton: ImageButton = itemView.findViewById(R.id.menuButton)
    private val reportedTextView: TextView = itemView.findViewById(R.id.reportedTextView)
    private val deletedTextView: TextView = itemView.findViewById(R.id.deletedTextView)

    fun bind(post: Post?) {
        mutablePost = post

        resetViews(post?.postStateLocal)

        if (post?.postStateLocal == PostState.DELETED || post?.postStateLocal == PostState.REPORTED) {
            // Do not need to process anything if post is deleted or reported
            return
        }

        mutablePost?.let {
            if (CustomAvatarImageView.defaultAvatar == null) {
                CustomAvatarImageView.defaultAvatar = ContextCompat.getDrawable(context, R.drawable.default_avatar)
            }

            GlideApp.with(context)
                .load(it.author?.avatarUri)
                .signature(CustomCacheKeyGenerator(it.author?.avatarUri ?: ""))
                .placeholder(CustomAvatarImageView.defaultAvatar)
                .circleCrop()
                .into(authorAvatar)

            authorName.text = it.author?.name ?: context.getString(R.string.unknown_author)
            postDate.text = getRelativeTime(it.createdAt)
            postText.text = it.caption

            if (it.caption.isNullOrEmpty()) {
                postText.visibility = View.GONE
            } else { postText.visibility = View.VISIBLE }

            when (it.postType) {
                "PRIMARY" -> {
                    it.attachment?.uri?.let { uri ->
                        postImage.visibility = View.VISIBLE

                        // Calculate the height based on the aspect ratio
                        val aspectRatio = it.attachment.height?.toFloat()?.div(
                            it.attachment.width ?: 1
                        ) ?: 0f
                        val calculatedHeight = (screenWidth * aspectRatio).toInt()

                        // Set the maxHeight constraint
                        val maxHeightInPx = dpToPx(context, 350f)
                        val finalHeight = if (calculatedHeight > maxHeightInPx) maxHeightInPx else calculatedHeight

                        // Update the ImageView's height
                        val layoutParams = postImage.layoutParams
                        layoutParams.height = finalHeight
                        postImage.layoutParams = layoutParams

                        // Load the image using Glide with CustomCacheKeyGenerator
                        GlideApp.with(context)
                            .load(uri)
                            .signature(CustomCacheKeyGenerator(uri))
                            .centerCrop()
                            .into(postImage)
                    }
                }

                "SHORT" -> {
                    customVideoPlayer.visibility = View.VISIBLE
                    it.attachment?.uri?.let { uri -> customVideoPlayer.setVideoUri(uri) }
                }
            }

            if (it.likeCount!! > 0) {
                likeCountText.visibility = View.VISIBLE
                likeCountText.text = it.likeCount.toString()
            }

            if (it.commentCount!! > 0) {
                commentCountText.visibility = View.VISIBLE
                commentCountText.text = it.commentCount.toString()
            }
        }

        sharedViewModel.user.observe(lifecycleOwner) { user ->
            if (mutablePost != null) {
                if (user.likedPosts?.contains(post?.id) == true) {
                    likeButton.visibility = View.GONE
                    alreadyLikedButton.visibility = View.VISIBLE
                } else {
                    likeButton.visibility = View.VISIBLE
                    alreadyLikedButton.visibility = View.GONE
                }
            }
        }

        authorName.setOnClickListener {
            if (post != null) {
                post.author?.id?.let { userId -> onAuthorClickListener.onAuthorClick(userId) }
            }
        }

        authorAvatar.setOnClickListener {
            if (post != null) {
                post.author?.id?.let { userId -> onAuthorClickListener.onAuthorClick(userId) }
            }
        }

        menuButton.setOnClickListener {
            showPopupMenu(it)
        }

        commentButton.setOnClickListener {
            post?.let { item -> onCommentClickListener.onCommentClick(item) }
        }

        likeButton.setOnClickListener {
            likeButton.visibility = View.GONE
            alreadyLikedButton.visibility = View.VISIBLE
            mutablePost = post?.copy(likeCount = post.likeCount?.plus(1))
            updateLikeCountText()

            if (post != null) {
                likeButtonClickListener.onLikeButtonClick(post.id) { isLiked ->
                    if (!isLiked) {
                        likeButton.visibility = View.VISIBLE
                        alreadyLikedButton.visibility = View.GONE
                        mutablePost = post.copy(likeCount = post.likeCount?.minus(1))
                        updateLikeCountText()
                    } else {
                        val updatedUser = sharedViewModel.user.value?.copy()
                        post.id.let { it1 -> updatedUser?.likedPosts?.add(it1) }
                        if (updatedUser != null) {
                            sharedViewModel.updateUser(updatedUser)
                        }
                    }
                }
            }
        }

        alreadyLikedButton.setOnClickListener {
            likeButton.visibility = View.VISIBLE
            alreadyLikedButton.visibility = View.GONE
            mutablePost = post?.copy(likeCount = post.likeCount?.minus(1))
            updateLikeCountText()

            if (post != null) {
                likeButtonClickListener.onUnlikeButtonClick(post.id) { isUnliked ->
                    if (!isUnliked) {
                        likeButton.visibility = View.GONE
                        alreadyLikedButton.visibility = View.VISIBLE
                        mutablePost = post.copy(likeCount = post.likeCount?.plus(1))
                        updateLikeCountText()
                    } else {
                        val updatedUser = sharedViewModel.user.value?.copy()
                        updatedUser?.likedPosts?.remove(post.id)
                        if (updatedUser != null) {
                            sharedViewModel.updateUser(updatedUser)
                        }
                    }
                }
            }
        }

        shareButton.setOnClickListener {
            sharePost(post)
        }
    }

    private fun updateLikeCountText() {
        likeCountText.visibility = if (mutablePost?.likeCount != null && mutablePost?.likeCount!! > 0) View.VISIBLE else View.GONE
        likeCountText.text = mutablePost?.likeCount?.toString()
    }

    // Add this interface to your PostView class
    interface OnAuthorClickListener {
        fun onAuthorClick(userId: String)
    }

    interface OnCommentClickListener {
        fun onCommentClick(post: Post)
    }

    interface OnLikeButtonClickListener {
        fun onLikeButtonClick(postId: String, onResult: (Boolean) -> Unit)
        fun onUnlikeButtonClick(postId: String, onResult: (Boolean) -> Unit)
    }

    interface PostMenuActionListener {
        fun onReportClick(post: Post)
        fun onDeleteClick(post: Post)
    }

    private fun sharePost(post: Post?) {
        post?.let {
            val url = "https://www.headsup.city/post-description/${it.id}"
            val title = "HeadsUp Post"
            var message = ""

            when (it.postType) {
                "PRIMARY", "SHORT", "JOB" -> {
                    message = "${it.caption} $url"
                }
                "EVENT" -> {
                    message = "${it.event?.description} $url"
                }
            }

            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, title)
            context.startActivity(shareIntent)
        }
    }

    private fun showPopupMenu(view: View) {
        val wrapper = ContextThemeWrapper(context, R.style.PostPopupMenu)
        val popup = PopupMenu(wrapper, view)

        sharedViewModel.user.value?.let { user ->
            if (user.id == mutablePost?.author?.id) {
                popup.inflate(R.menu.post_author_popup_menu)
            } else {
                popup.inflate(R.menu.post_viewer_popup_menu)
            }
        }

        popup.show()

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.report -> {
                    // Handle Report action
                    mutablePost?.let { post ->
                        post.author?.name?.let { authorName ->
                            val dialog = AlertDialog.Builder(context)
                                .setTitle("Report Post")
                                .setMessage("Do you want to report this post by $authorName?")
                                .setPositiveButton("Report") { _, _ ->
                                    postMenuActionListener.onReportClick(post)
                                    post.postStateLocal = PostState.REPORTED
                                    resetViews(PostState.REPORTED)
                                }
                                .setNegativeButton("Cancel", null)
                                .create()

                            dialog.show()
                        }
                    }
                    true
                }
                R.id.delete -> {
                    mutablePost?.let { post ->
                        val dialog = AlertDialog.Builder(context)
                            .setTitle("Delete Post")
                            .setMessage("Do you really want to delete this post?")
                            .setPositiveButton("Delete") { _, _ ->
                                postMenuActionListener.onDeleteClick(post)
                                post.postStateLocal = PostState.DELETED
                                resetViews(PostState.DELETED)
                            }
                            .setNegativeButton("Cancel", null)
                            .create()

                        dialog.show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun resetViews(state: PostState?) {
        when (state) {
            PostState.REPORTED -> {
                hideAllViews()
                reportedTextView.visibility = View.VISIBLE
            }

            PostState.DELETED -> {
                hideAllViews()
                deletedTextView.visibility = View.VISIBLE
            }

            else -> {
                authorAvatar.visibility = View.VISIBLE
                authorName.visibility = View.VISIBLE
                postDate.visibility = View.VISIBLE
                likeButton.visibility = View.VISIBLE
                alreadyLikedButton.visibility = View.VISIBLE
                commentButton.visibility = View.VISIBLE
                shareButton.visibility = View.VISIBLE
                menuButton.visibility = View.VISIBLE
                postHeader.visibility = View.VISIBLE
                postBody.visibility = View.VISIBLE
                likeCountText.visibility = View.GONE
                commentCountText.visibility = View.GONE
                postImage.visibility = View.GONE
                postText.visibility = View.GONE
                customVideoPlayer.visibility = View.GONE
                reportedTextView.visibility = View.GONE
                deletedTextView.visibility = View.GONE
                viewUtils.increaseTouchableArea(menuButton, R.dimen.size_32_button_inc)
            }
        }
    }

    private fun hideAllViews() {
        authorAvatar.visibility = View.GONE
        authorName.visibility = View.GONE
        postDate.visibility = View.GONE
        postImage.visibility = View.GONE
        postText.visibility = View.GONE
        customVideoPlayer.visibility = View.GONE
        likeButton.visibility = View.GONE
        alreadyLikedButton.visibility = View.GONE
        likeCountText.visibility = View.GONE
        commentButton.visibility = View.GONE
        commentCountText.visibility = View.GONE
        shareButton.visibility = View.GONE
        menuButton.visibility = View.GONE
        postHeader.visibility = View.GONE
        postBody.visibility = View.GONE
        reportedTextView.visibility = View.GONE
        deletedTextView.visibility = View.GONE
    }

    fun onDetachedFromWindow() {
        customVideoPlayer.resetPlayer()
    }
}
