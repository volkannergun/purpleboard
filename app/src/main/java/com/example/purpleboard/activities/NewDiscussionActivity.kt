package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityNewDiscussionBinding
import com.example.purpleboard.models.NewDiscussionRequest
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class NewDiscussionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewDiscussionBinding
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewDiscussionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = getString(R.string.new_discussion_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.buttonCreate.setOnClickListener {
            createDiscussion()
        }
    }

    private fun createDiscussion() {
        val topic = binding.editTextDiscussionTopic.text.toString().trim()
        val description = binding.editTextDiscussionDescription.text.toString().trim()

        if (topic.isEmpty()) {
            binding.tilDiscussionTopic.error = "Topic cannot be empty"
            return
        } else {
            binding.tilDiscussionTopic.error = null
        }

        if (description.isEmpty()) {
            binding.tilDiscussionDescription.error = "Description cannot be empty"
            return
        } else {
            binding.tilDiscussionDescription.error = null
        }

        val studentId = prefsHelper.getUserId()
        if (studentId == -1) {
            showToast("Error: User not logged in properly.")
            // Optionally navigate to login
            return
        }

        showLoading(true)
        val newDiscussionRequest = NewDiscussionRequest(topic, description)

        lifecycleScope.launch {
            try {
                // Pass studentId via header as defined in ApiService
                val response = RetrofitClient.apiService.createDiscussion(studentId, newDiscussionRequest)

                if (response.isSuccessful && response.body() != null) {
                    showToast("Discussion created successfully!")
                    setResult(Activity.RESULT_OK) // Signal to DiscussionsActivity to refresh
                    finish() // Close this activity
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Failed to create discussion"
                    showToast(errorMsg)
                    Log.e("NewDiscussionActivity", "API Error: ${response.code()} - $errorMsg")
                }
            } catch (e: Exception) {
                showToast("Error creating discussion: ${e.message}")
                Log.e("NewDiscussionActivity", "Exception: ", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarNewDiscussion.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonCreate.isEnabled = !isLoading
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}