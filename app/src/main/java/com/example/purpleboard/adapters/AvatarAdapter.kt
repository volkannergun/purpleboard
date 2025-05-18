package com.example.purpleboard.adapters // Adjust package name

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ItemAvatarBinding

class AvatarAdapter(
    private val context: Context,
    private val avatarNames: List<String>, // e.g., ["avatar1", "avatar2", ..., "avatar20"]
    private val onAvatarClick: (String) -> Unit
) : RecyclerView.Adapter<AvatarAdapter.AvatarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvatarViewHolder {
        val binding = ItemAvatarBinding.inflate(LayoutInflater.from(context), parent, false)
        return AvatarViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AvatarViewHolder, position: Int) {
        val avatarName = avatarNames[position]
        holder.bind(avatarName)
    }

    override fun getItemCount(): Int = avatarNames.size

    inner class AvatarViewHolder(private val binding: ItemAvatarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onAvatarClick(avatarNames[position])
                }
            }
        }

        fun bind(avatarName: String) {
            val avatarResId = context.resources.getIdentifier(
                avatarName, "drawable", context.packageName
            )
            if (avatarResId != 0) {
                Glide.with(context)
                    .load(avatarResId)
                    .placeholder(R.drawable.ic_profile_placeholder) // Generic placeholder
                    .error(R.drawable.ic_profile_placeholder)
                    .circleCrop() // Or remove if avatars are not meant to be circular here
                    .into(binding.imageViewAvatarOption)
            } else {
                // Fallback if resource name is wrong or file missing
                binding.imageViewAvatarOption.setImageResource(R.drawable.ic_profile_placeholder)
            }
        }
    }
}