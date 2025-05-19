package com.example.purpleboard.activities // Adjust package

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.databinding.ActivityNewAssignmentBinding
import com.example.purpleboard.models.NewAssignmentRequest
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class NewAssignmentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewAssignmentBinding
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewAssignmentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = "Create New Assignment" // Or use a string resource
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonCreateNewAssignment.setOnClickListener {
            createNewAssignment()
        }
    }

    private fun createNewAssignment() {
        val topic = binding.editTextAssignmentTopic.text.toString().trim()
        val description = binding.editTextAssignmentDescription.text.toString().trim()
        val pointsStr = binding.editTextAssignmentPoints.text.toString().trim()

        if (topic.isEmpty()) {
            binding.tilAssignmentTopic.error = "Topic cannot be empty"
            return
        } else {
            binding.tilAssignmentTopic.error = null
        }

        if (description.isEmpty()) {
            binding.tilAssignmentDescription.error = "Description cannot be empty"
            return
        } else {
            binding.tilAssignmentDescription.error = null
        }

        val pointsValue: Int
        if (pointsStr.isEmpty()) {
            binding.tilAssignmentPoints.error = "Points value cannot be empty"
            return
        } else {
            try {
                pointsValue = pointsStr.toInt()
                if (pointsValue < 0) {
                    binding.tilAssignmentPoints.error = "Points must be non-negative"
                    return
                }
                binding.tilAssignmentPoints.error = null
            } catch (e: NumberFormatException) {
                binding.tilAssignmentPoints.error = "Invalid points value"
                return
            }
        }

        val adminId = prefsHelper.getUserId() // Assuming admin is logged in
        if (adminId == -1) {
            showToast("Error: Admin user not identified.")
            return
        }

        showLoading(true)
        val newAssignmentRequest = NewAssignmentRequest(topic, description, pointsValue)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.createAssignment(adminId, newAssignmentRequest)
                if (response.isSuccessful) {
                    showToast("Assignment created successfully!")
                    setResult(Activity.RESULT_OK) // Signal to AssignmentsActivity to refresh
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Failed to create assignment"
                    showToast("Error: $errorMsg")
                    Log.e("NewAssignmentActivity", "API Error: ${response.code()} - $errorMsg")
                }
            } catch (e: Exception) {
                showToast("Creation failed: ${e.message}")
                Log.e("NewAssignmentActivity", "Exception: ", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarNewAssignment.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonCreateNewAssignment.isEnabled = !isLoading
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}