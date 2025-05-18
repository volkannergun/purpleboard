package com.example.purpleboard.adapters // Adjust package name

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ItemLeaderboardEntryBinding
import com.example.purpleboard.databinding.ItemLeaderboardEntryTopBinding
import com.example.purpleboard.models.LeaderboardEntry

class LeaderboardAdapter(
    private var entries: List<LeaderboardEntry>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var context: Context

    companion object {
        private const val VIEW_TYPE_TOP_STUDENT = 0
        private const val VIEW_TYPE_REGULAR_STUDENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && entries.isNotEmpty()) { // Assuming list is sorted and first is top
            VIEW_TYPE_TOP_STUDENT
        } else {
            VIEW_TYPE_REGULAR_STUDENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        val inflater = LayoutInflater.from(context)
        return when (viewType) {
            VIEW_TYPE_TOP_STUDENT -> {
                val binding = ItemLeaderboardEntryTopBinding.inflate(inflater, parent, false)
                TopStudentViewHolder(binding)
            }
            VIEW_TYPE_REGULAR_STUDENT -> {
                val binding = ItemLeaderboardEntryBinding.inflate(inflater, parent, false)
                RegularStudentViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val entry = entries[position]
        when (holder) {
            is TopStudentViewHolder -> holder.bind(entry, position + 1) // Pass rank
            is RegularStudentViewHolder -> holder.bind(entry, position + 1) // Pass rank
        }
    }

    override fun getItemCount(): Int = entries.size

    fun updateLeaderboard(newEntries: List<LeaderboardEntry>) {
        // Assuming the newEntries list is already sorted by points descending from the server
        entries = newEntries
        notifyDataSetChanged() // Consider DiffUtil
    }

    // --- ViewHolders ---

    inner class TopStudentViewHolder(private val binding: ItemLeaderboardEntryTopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: LeaderboardEntry, rank: Int) {
            binding.textViewRankTop.text = "$rank."
            binding.textViewLeaderboardNameTop.text = entry.name
            binding.textViewLeaderboardPointsTop.text = entry.points.toString()

            loadAvatar(entry.avatarName, binding.imageViewLeaderboardAvatarTop)
        }
    }

    inner class RegularStudentViewHolder(private val binding: ItemLeaderboardEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(entry: LeaderboardEntry, rank: Int) {
            binding.textViewRank.text = "$rank."
            binding.textViewLeaderboardName.text = entry.name
            binding.textViewLeaderboardPoints.text = entry.points.toString()

            loadAvatar(entry.avatarName, binding.imageViewLeaderboardAvatar)
        }
    }

    private fun loadAvatar(avatarName: String?, imageView: android.widget.ImageView) {
        if (!avatarName.isNullOrEmpty()) {
            val avatarResId = context.resources.getIdentifier(
                avatarName, "drawable", context.packageName
            )
            if (avatarResId != 0) {
                Glide.with(context)
                    .load(avatarResId)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } else {
            imageView.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }
}