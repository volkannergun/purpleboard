package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purpleboard.R
import com.example.purpleboard.adapters.DiscussionAdapter
import com.example.purpleboard.databinding.ActivityDiscussionsBinding
import com.example.purpleboard.models.Discussion
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class DiscussionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiscussionsBinding
    private lateinit var discussionAdapter: DiscussionAdapter
    private var discussionList = mutableListOf<Discussion>()
    private lateinit var prefsHelper: SharedPreferencesHelper


    // ActivityResultLauncher for when a new discussion is created
    private val newDiscussionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // New discussion was created, refresh the list
                fetchDiscussions()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = getString(R.string.discussions_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Show Up button

        setupRecyclerView()

        binding.buttonCreateDiscussion.setOnClickListener {
            val intent = Intent(this, NewDiscussionActivity::class.java)
            newDiscussionResultLauncher.launch(intent)
        }

        fetchDiscussions()
    }

    private fun setupRecyclerView() {
        discussionAdapter = DiscussionAdapter(discussionList) { discussion ->
            // Handle discussion item click -> Navigate to DiscussionDetailActivity
            val intent = Intent(this, DiscussionDetailActivity::class.java)
            intent.putExtra("DISCUSSION_ID", discussion.discussionId)
            intent.putExtra("DISCUSSION_TOPIC", discussion.topic) // Pass topic for title
            startActivity(intent)
        }
        binding.recyclerViewDiscussions.apply {
            layoutManager = LinearLayoutManager(this@DiscussionsActivity)
            adapter = discussionAdapter
        }
    }

    private fun fetchDiscussions() {
        showLoading(true)
        binding.textViewNoDiscussions.hide()

        lifecycleScope.launch {
            try {
                // Pass currentUserId if your API uses it to identify user's own posts or for other logic
                // val currentUserId = prefsHelper.getUserId().takeIf { it != -1 }
                val response = RetrofitClient.apiService.getAllDiscussions(/*currentUserId = currentUserId*/)

                if (response.isSuccessful && response.body() != null) {
                    val fetchedDiscussions = response.body()!!
                    discussionList.clear()
                    discussionList.addAll(fetchedDiscussions.sortedByDescending { it.createdAt }) // Sort new to old
                    discussionAdapter.updateDiscussions(discussionList)

                    if (discussionList.isEmpty()) {
                        binding.textViewNoDiscussions.show()
                    } else {
                        binding.textViewNoDiscussions.hide()
                    }

                } else {
                    showToast("Failed to load discussions: ${response.message()}")
                    Log.e("DiscussionsActivity", "API Error: ${response.code()} - ${response.message()}")
                    if (discussionList.isEmpty()) binding.textViewNoDiscussions.show()
                }
            } catch (e: Exception) {
                showToast("Error fetching discussions: ${e.message}")
                Log.e("DiscussionsActivity", "Exception: ", e)
                if (discussionList.isEmpty()) binding.textViewNoDiscussions.show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarDiscussions.show()
            binding.recyclerViewDiscussions.hide() // Hide list while loading
        } else {
            binding.progressBarDiscussions.hide()
            binding.recyclerViewDiscussions.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}