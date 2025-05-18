package com.example.purpleboard.adapters // Adjust package name

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ItemAssignmentBinding
import com.example.purpleboard.models.Assignment

class AssignmentAdapter(
    private var assignments: List<Assignment>,
    private val onItemClick: (Assignment) -> Unit
) : RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        val assignment = assignments[position]
        holder.bind(assignment)
    }

    override fun getItemCount(): Int = assignments.size

    fun updateAssignments(newAssignments: List<Assignment>) {
        assignments = newAssignments
        notifyDataSetChanged() // Consider DiffUtil for better performance
    }

    inner class AssignmentViewHolder(private val binding: ItemAssignmentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(assignments[position])
                }
            }
        }

        fun bind(assignment: Assignment) {
            binding.textViewAssignmentTopic.text = assignment.topic
            binding.textViewAssignmentDescription.text = assignment.description
            binding.textViewAssignmentPoints.text = "Points: ${assignment.pointsValue}"

            if (assignment.isCompletedByCurrentUser) {
                binding.cardViewAssignmentItem.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.app_green_completed)
                )
                // Optionally change text color for better contrast on green
                binding.textViewAssignmentTopic.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.textViewAssignmentDescription.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
                binding.textViewAssignmentPoints.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            } else {
                // Reset to default background/text colors if not completed
                // The default MaterialCardView background will apply or you can set one explicitly
                binding.cardViewAssignmentItem.setCardBackgroundColor(
                    ContextCompat.getColor(binding.root.context, R.color.white) // Or your card's default background
                )
                binding.textViewAssignmentTopic.setTextColor(ContextCompat.getColor(binding.root.context, R.color.app_gray_dark_text)) // Default text color
                binding.textViewAssignmentDescription.setTextColor(ContextCompat.getColor(binding.root.context, R.color.app_gray_dark_text))
                binding.textViewAssignmentPoints.setTextColor(ContextCompat.getColor(binding.root.context, R.color.app_purple_primary)) // Default points color
            }
        }
    }
}