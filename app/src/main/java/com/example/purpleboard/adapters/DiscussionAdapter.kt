package com.example.purpleboard.adapters // Adjust package name

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ItemDiscussionBinding
import com.example.purpleboard.models.Discussion
import com.example.purpleboard.utils.Constants // For avatar loading if needed, not directly used here yet

class DiscussionAdapter(
    private var discussions: List<Discussion>,
    private val onItemClick: (Discussion) -> Unit
) : RecyclerView.Adapter<DiscussionAdapter.DiscussionViewHolder>() {

    private lateinit var context: Context // To get resources like drawable identifiers

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        context = parent.context
        val binding = ItemDiscussionBinding.inflate(LayoutInflater.from(context), parent, false)
        return DiscussionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        val discussion = discussions[position]
        holder.bind(discussion)
    }

    override fun getItemCount(): Int = discussions.size

    fun updateDiscussions(newDiscussions: List<Discussion>) {
        discussions = newDiscussions
        notifyDataSetChanged() // Consider using DiffUtil for better performance
    }

    inner class DiscussionViewHolder(private val binding: ItemDiscussionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(discussions[position])
                }
            }
        }

        fun bind(discussion: Discussion) {
            binding.textViewDiscussionTopic.text = discussion.topic
            binding.textViewDiscussionDescription.text = discussion.description
            binding.textViewAuthorName.text = discussion.studentName ?: "Unknown User"
            binding.textViewDiscussionDate.text = discussion.createdAt // Consider formatting this date

            binding.textViewReplyCount.text = "${discussion.replyCount ?: 0} Replies" // Handle null replyCount

            // Load author avatar
            // Assuming avatarName is like "avatar1", "avatar2", etc. and these are in res/drawable/avatars
            if (!discussion.studentAvatar.isNullOrEmpty()) {
                val avatarResId = context.resources.getIdentifier(
                    discussion.studentAvatar, "drawable", context.packageName
                )
                if (avatarResId != 0) { // 0 means resource not found
                    Glide.with(context)
                        .load(avatarResId)
                        .placeholder(R.drawable.ic_profile_placeholder) // Placeholder
                        .error(R.drawable.ic_profile_placeholder)       // Error image
                        .circleCrop()
                        .into(binding.imageViewAuthorAvatar)
                } else {
                    binding.imageViewAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
                }
            } else {
                binding.imageViewAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }
    }
}