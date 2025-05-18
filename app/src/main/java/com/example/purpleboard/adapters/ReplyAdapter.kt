package com.example.purpleboard.adapters // Adjust package name

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ItemReplyBinding
import com.example.purpleboard.models.Reply

class ReplyAdapter(
    private var replies: List<Reply>
) : RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {
        context = parent.context
        val binding = ItemReplyBinding.inflate(LayoutInflater.from(context), parent, false)
        return ReplyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {
        val reply = replies[position]
        holder.bind(reply)
    }

    override fun getItemCount(): Int = replies.size

    fun updateReplies(newReplies: List<Reply>) {
        replies = newReplies
        notifyDataSetChanged() // Consider DiffUtil
    }

    fun addReply(reply: Reply) {
        val mutableReplies = replies.toMutableList()
        mutableReplies.add(0, reply) // Add to the top (newest first)
        replies = mutableReplies
        notifyItemInserted(0)
        // If you want to scroll to top: recyclerView.scrollToPosition(0) in activity
    }


    inner class ReplyViewHolder(private val binding: ItemReplyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(reply: Reply) {
            binding.textViewReplyAuthorName.text = reply.studentName ?: "User"
            binding.textViewReplyText.text = reply.replyText
            binding.textViewReplyDate.text = reply.createdAt // Consider formatting

            // Load author avatar
            if (!reply.studentAvatar.isNullOrEmpty()) {
                val avatarResId = context.resources.getIdentifier(
                    reply.studentAvatar, "drawable", context.packageName
                )
                if (avatarResId != 0) {
                    Glide.with(context)
                        .load(avatarResId)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(binding.imageViewReplyAuthorAvatar)
                } else {
                    binding.imageViewReplyAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
                }
            } else {
                binding.imageViewReplyAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }
    }
}