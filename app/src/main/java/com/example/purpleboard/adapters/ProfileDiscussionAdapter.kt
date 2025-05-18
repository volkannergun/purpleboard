package com.example.purpleboard.adapters // Adjust package name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.purpleboard.databinding.ItemProfileDiscussionBinding
import com.example.purpleboard.models.Discussion

class ProfileDiscussionAdapter(
    private var discussions: List<Discussion>,
    private val onItemClick: (Discussion) -> Unit
) : RecyclerView.Adapter<ProfileDiscussionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProfileDiscussionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(discussions[position])
    }

    override fun getItemCount(): Int = discussions.size

    fun updateDiscussions(newDiscussions: List<Discussion>) {
        discussions = newDiscussions
        notifyDataSetChanged() // Consider DiffUtil
    }

    inner class ViewHolder(private val binding: ItemProfileDiscussionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(discussions[position])
                }
            }
        }

        fun bind(discussion: Discussion) {
            binding.textViewProfileDiscussionTopic.text = discussion.topic
            binding.textViewProfileDiscussionDescriptionPreview.text = discussion.description
            binding.textViewProfileDiscussionDate.text = "Created: ${discussion.createdAt}" // Format date
        }
    }
}