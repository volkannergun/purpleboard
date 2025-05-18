package com.example.purpleboard.activities // Adjust package name

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.adapters.ReplyAdapter
import com.example.purpleboard.databinding.ActivityDiscussionDetailBinding
import com.example.purpleboard.models.Discussion
import com.example.purpleboard.models.NewReplyRequest
import com.example.purpleboard.models.Reply
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.hideKeyboard
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class DiscussionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiscussionDetailBinding
    private var discussionId: Int = -1
    private var discussionTopic: String? = null
    private lateinit var replyAdapter: ReplyAdapter
    private var replyList = mutableListOf<Reply>()
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiscussionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        discussionId = intent.getIntExtra("DISCUSSION_ID", -1)
        discussionTopic = intent.getStringExtra("DISCUSSION_TOPIC")

        supportActionBar?.title = discussionTopic ?: "Discussion"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (discussionId == -1) {
            showToast("Error: Discussion ID not found.")
            finish()
            return
        }

        setupRecyclerView()
        fetchDiscussionDetails()
        fetchReplies()

        binding.buttonSendReply.setOnClickListener {
            postNewReply()
        }
    }

    private fun setupRecyclerView() {
        replyAdapter = ReplyAdapter(replyList)
        binding.recyclerViewReplies.apply {
            layoutManager = LinearLayoutManager(this@DiscussionDetailActivity)
            adapter = replyAdapter
            // isNestedScrollingEnabled = false // Already in XML, but good to be aware of for ScrollView
        }
    }

    private fun fetchDiscussionDetails() {
        showLoading(true)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getDiscussionDetails(discussionId)
                if (response.isSuccessful && response.body() != null) {
                    displayDiscussion(response.body()!!)
                } else {
                    showToast("Failed to load discussion details: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Error loading details: ${e.message}")
                Log.e("DiscussionDetail", "Exception details: ", e)
            }
            // Loading for replies will handle overall loading state
        }
    }

    private fun displayDiscussion(discussion: Discussion) {
        binding.textViewDetailTopic.text = discussion.topic
        binding.textViewDetailAuthorName.text = discussion.studentName ?: "Unknown User"
        binding.textViewDetailDate.text = discussion.createdAt // Format date
        binding.textViewDetailDescription.text = discussion.description

        // Load author avatar
        if (!discussion.studentAvatar.isNullOrEmpty()) {
            val avatarResId = resources.getIdentifier(discussion.studentAvatar, "drawable", packageName)
            if (avatarResId != 0) {
                Glide.with(this)
                    .load(avatarResId)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .circleCrop()
                    .into(binding.imageViewDetailAuthorAvatar)
            } else {
                binding.imageViewDetailAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
            }
        } else {
            binding.imageViewDetailAuthorAvatar.setImageResource(R.drawable.ic_profile_placeholder)
        }
    }

    private fun fetchReplies() {
        showLoading(true) // Could be combined with detail loading
        binding.textViewNoReplies.hide()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getDiscussionReplies(discussionId)
                if (response.isSuccessful && response.body() != null) {
                    val fetchedReplies = response.body()!!
                    replyList.clear()
                    replyList.addAll(fetchedReplies.sortedByDescending { it.createdAt }) // Newest first
                    replyAdapter.updateReplies(replyList)

                    if (replyList.isEmpty()) {
                        binding.textViewNoReplies.show()
                    } else {
                        binding.textViewNoReplies.hide()
                    }
                } else {
                    showToast("Failed to load replies: ${response.message()}")
                    if (replyList.isEmpty()) binding.textViewNoReplies.show()
                }
            } catch (e: Exception) {
                showToast("Error loading replies: ${e.message}")
                Log.e("DiscussionDetail", "Exception replies: ", e)
                if (replyList.isEmpty()) binding.textViewNoReplies.show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun postNewReply() {
        val replyText = binding.editTextReply.text.toString().trim()
        if (replyText.isEmpty()) {
            showToast("Reply cannot be empty.")
            return
        }

        val studentId = prefsHelper.getUserId()
        if (studentId == -1) {
            showToast("Error: User not identified. Please login again.")
            return
        }

        binding.buttonSendReply.isEnabled = false // Disable button during API call
        binding.editTextReply.isEnabled = false

        val newReplyRequest = NewReplyRequest(replyText)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.postReply(discussionId, studentId, newReplyRequest)
                if (response.isSuccessful && response.body() != null) {
                    val newReply = response.body()!!
                    replyAdapter.addReply(newReply) // Add to adapter (top of the list)
                    binding.recyclerViewReplies.scrollToPosition(0) // Scroll to new reply
                    binding.editTextReply.text?.clear()
                    binding.editTextReply.clearFocus()
                    binding.root.hideKeyboard()
                    binding.textViewNoReplies.hide() // Hide "no replies" message if it was visible
                    showToast("Reply posted!")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: response.message() ?: "Failed to post reply"
                    showToast(errorMsg)
                    Log.e("DiscussionDetail", "API Error reply: ${response.code()} - $errorMsg")
                }
            } catch (e: Exception) {
                showToast("Error posting reply: ${e.message}")
                Log.e("DiscussionDetail", "Exception post reply: ", e)
            } finally {
                binding.buttonSendReply.isEnabled = true
                binding.editTextReply.isEnabled = true
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        binding.progressBarDiscussionDetail.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}