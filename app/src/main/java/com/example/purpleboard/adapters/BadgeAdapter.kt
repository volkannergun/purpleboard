package com.example.purpleboard.adapters // Adjust package name

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.purpleboard.databinding.ItemBadgeBinding

// Simple data class for a badge
data class BadgeItem(val name: String, val iconResId: Int)

class BadgeAdapter(
    private val context: Context,
    private val badges: List<BadgeItem>
) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(badges[position])
    }

    override fun getItemCount(): Int = badges.size

    inner class BadgeViewHolder(private val binding: ItemBadgeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(badge: BadgeItem) {
            binding.imageViewBadge.setImageResource(badge.iconResId)
            binding.textViewBadgeName.text = badge.name
        }
    }
}