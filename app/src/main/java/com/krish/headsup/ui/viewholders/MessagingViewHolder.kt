package com.krish.headsup.ui.viewholders

import android.view.Gravity
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.krish.headsup.R
import com.krish.headsup.databinding.MessagingListBinding
import com.krish.headsup.model.Message
import java.text.SimpleDateFormat
import java.util.Locale

class MessagingViewHolder(private val binding: MessagingListBinding, private val selfUserId: String?) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(message: Message) {
        binding.apply {
            messageBody.text = message.text
            messageTime.text = convertToTimeFormat(message.createdAt)

            if (message.author.id == selfUserId) {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.root as ConstraintLayout)
                constraintSet.clear(R.id.messageContainer, ConstraintSet.START)
                constraintSet.connect(R.id.messageContainer, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.clear(R.id.messageTime, ConstraintSet.START)
                constraintSet.connect(R.id.messageTime, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.applyTo(binding.root as ConstraintLayout)
                messageBody.setBackgroundResource(R.drawable.background_current_user_message)
            } else {
                val constraintSet = ConstraintSet()
                constraintSet.clone(binding.root as ConstraintLayout)
                constraintSet.clear(R.id.messageContainer, ConstraintSet.END)
                constraintSet.connect(R.id.messageContainer, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.clear(R.id.messageTime, ConstraintSet.END)
                constraintSet.connect(R.id.messageTime, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.applyTo(binding.root as ConstraintLayout)
                messageBody.setBackgroundResource(R.drawable.background_other_user_message)
            }
        }
    }

    private fun convertToTimeFormat(dateTime: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = formatter.parse(dateTime)
        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormatter.format(date)
    }
}
