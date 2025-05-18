package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityAssignmentDetailBinding
import com.example.purpleboard.models.Assignment
import com.example.purpleboard.models.SubmitAssignmentRequest
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.hideKeyboard
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class AssignmentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignmentDetailBinding
    private var assignmentId: Int = -1
    private var assignmentTopic: String? = null
    private var isAlreadyCompleted: Boolean = false
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignmentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        assignmentId = intent.getIntExtra("ASSIGNMENT_ID", -1)
        assignmentTopic = intent.getStringExtra("ASSIGNMENT_TOPIC")
        isAlreadyCompleted = intent.getBooleanExtra("IS_COMPLETED", false)


        supportActionBar?.title = assignmentTopic ?: "Assignment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (assignmentId == -1) {
            showToast("Error: Assignment ID not found.")
            finish()
            return
        }

        fetchAssignmentDetails() // Load details even if completed, to show description

        if (isAlreadyCompleted) {
            showCompletedState()
        }

        binding.buttonSubmitAssignment.setOnClickListener {
            if (!isAlreadyCompleted) {
                submitAssignment()
            } else {
                showToast("You have already completed this assignment.")
            }
        }
    }

    private fun fetchAssignmentDetails() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getAssignmentDetails(assignmentId)
                if (response.isSuccessful && response.body() != null) {
                    displayAssignment(response.body()!!)
                } else {
                    showToast("Failed to load assignment details: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Error loading assignment: ${e.message}")
                Log.e("AssignmentDetail", "Exception details: ", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun displayAssignment(assignment: Assignment) {
        binding.textViewAssignmentDetailTopic.text = assignment.topic
        binding.textViewAssignmentDetailDescription.text = assignment.description
        binding.textViewAssignmentDetailPoints.text = "Points: ${assignment.pointsValue}"
    }

    private fun showCompletedState() {
        binding.textViewAssignmentStatus.text = "Assignment Completed!"
        binding.textViewAssignmentStatus.show()
        binding.tilAssignmentReply.isEnabled = false
        binding.editTextAssignmentReply.isEnabled = false
        binding.buttonSubmitAssignment.isEnabled = false
        binding.buttonSubmitAssignment.text = "Completed"
        binding.buttonSubmitAssignment.alpha = 0.7f
        binding.layoutReplySection.visibility = View.GONE // Hide reply section
    }


    private fun submitAssignment() {
        val replyText = binding.editTextAssignmentReply.text.toString().trim()
        if (replyText.isEmpty()) {
            binding.tilAssignmentReply.error = "Your reply/solution cannot be empty."
            return
        } else {
            binding.tilAssignmentReply.error = null
        }

        val studentId = prefsHelper.getUserId()
        if (studentId == -1) {
            showToast("Error: User not identified. Please login again.")
            return
        }

        showLoading(true)
        val submitRequest = SubmitAssignmentRequest(replyText)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.completeAssignment(assignmentId, studentId, submitRequest)
                if (response.isSuccessful) { // Can be 200 OK with SimpleApiResponse or just 204 No Content
                    showToast(response.body()?.message ?: "Assignment submitted successfully!")
                    isAlreadyCompleted = true
                    showCompletedState() // Update UI to reflect completion

                    // Send result back to AssignmentsActivity to update list
                    val resultIntent = Intent()
                    resultIntent.putExtra("COMPLETED_ASSIGNMENT_ID", assignmentId)
                    setResult(Activity.RESULT_OK, resultIntent)
                    // No need to finish() immediately, user might want to review, but up to you.
                    // If you want to auto-close: finish()

                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Failed to submit assignment"
                    showToast(errorMsg)
                    Log.e("AssignmentDetail", "API Error submit: ${response.code()} - $errorMsg")
                }
            } catch (e: Exception) {
                showToast("Error submitting assignment: ${e.message}")
                Log.e("AssignmentDetail", "Exception submit: ", e)
            } finally {
                showLoading(false)
            }
        }
        binding.root.hideKeyboard()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarAssignmentDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonSubmitAssignment.isEnabled = !isLoading && !isAlreadyCompleted
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}