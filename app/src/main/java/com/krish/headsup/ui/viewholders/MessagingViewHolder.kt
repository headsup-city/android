package com.krish.headsup.ui.viewholders

import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.databinding.MessagingListBinding
import com.krish.headsup.model.Message
import com.krish.headsup.utils.convertToTimeFormat
class MessagingViewHolder(
    private val binding: MessagingListBinding,
    private val selfUserId: String?
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message, shouldShowTime: Boolean) {
        binding.apply {
            messageBody.text = message.text
            messageTime.text = if (shouldShowTime) convertToTimeFormat(message.createdAt) else ""
            messageTime.visibility = if (shouldShowTime) View.VISIBLE else View.GONE

            if (message.author.id == selfUserId) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.root)
                constraintSet.clear(R.id.messageContainer, ConstraintSet.START)
                constraintSet.connect(R.id.messageContainer, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.clear(R.id.messageTime, ConstraintSet.START)
                constraintSet.connect(R.id.messageTime, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.applyTo(binding.root)
                messageBodyWrapper.layoutParams = (messageBodyWrapper.layoutParams as LinearLayout.LayoutParams).apply {
                    gravity = Gravity.END
                }
                messageBody.setBackgroundResource(R.drawable.background_current_user_message)
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.root)
                constraintSet.clear(R.id.messageContainer, ConstraintSet.END)
                constraintSet.connect(R.id.messageContainer, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.clear(R.id.messageTime, ConstraintSet.END)
                constraintSet.connect(R.id.messageTime, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.applyTo(binding.root)
                messageBodyWrapper.layoutParams = (messageBodyWrapper.layoutParams as LinearLayout.LayoutParams).apply {
                    gravity = Gravity.START
                }
                messageBody.setBackgroundResource(R.drawable.background_other_user_message)
            }
        }
    }
}
