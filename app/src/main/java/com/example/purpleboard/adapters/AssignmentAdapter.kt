package com.example.purpleboard.adapters // Adjust package name

import android.view.LayoutInflater
import android.view.View // Import View for GONE/VISIBLE constants
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
                val position = bindingAdapterPosition // Use bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                    onItemClick(assignments[position])
                }
            }
        }

        fun bind(assignment: Assignment) {
            binding.textViewAssignmentTopic.text = assignment.topic

            // START: MODIFICATION TO DISPLAY ADMIN NAME
            if (!assignment.createdByAdminName.isNullOrEmpty()) {
                binding.textViewAssignmentAdminName.text = "Created by: ${assignment.createdByAdminName}"
                binding.textViewAssignmentAdminName.visibility = View.VISIBLE
            } else {
                // Decide how to handle if admin name is not available
                // Option 1: Hide the field
                binding.textViewAssignmentAdminName.visibility = View.GONE
                // Option 2: Show a default text
                // binding.textViewAssignmentAdminName.text = "Created by: System"
                // binding.textViewAssignmentAdminName.visibility = View.VISIBLE
            }
            // END: MODIFICATION TO DISPLAY ADMIN NAME

            binding.textViewAssignmentDescription.text = assignment.description
            binding.textViewAssignmentPoints.text = "Points: ${assignment.pointsValue}"

            val context = binding.root.context // Get context for fetching colors

            if (assignment.isCompletedByCurrentUser) {
                binding.cardViewAssignmentItem.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.app_green_completed)
                )
                // Optionally change text color for better contrast on green
                binding.textViewAssignmentTopic.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.textViewAssignmentAdminName.setTextColor(ContextCompat.getColor(context, R.color.white)) // Also set color for admin name
                binding.textViewAssignmentDescription.setTextColor(ContextCompat.getColor(context, R.color.white))
                binding.textViewAssignmentPoints.setTextColor(ContextCompat.getColor(context, R.color.white))
            } else {
                // Reset to default background/text colors if not completed
                binding.cardViewAssignmentItem.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.white) // Or your card's default background from styles
                )
                binding.textViewAssignmentTopic.setTextColor(ContextCompat.getColor(context, R.color.app_gray_dark_text)) // Default text color
                binding.textViewAssignmentAdminName.setTextColor(ContextCompat.getColor(context, R.color.app_gray_medium)) // Default color for admin name
                binding.textViewAssignmentDescription.setTextColor(ContextCompat.getColor(context, R.color.app_gray_dark_text))
                binding.textViewAssignmentPoints.setTextColor(ContextCompat.getColor(context, R.color.app_purple_primary)) // Default points color
            }
        }
    }
}