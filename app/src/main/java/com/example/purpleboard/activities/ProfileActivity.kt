package com.example.purpleboard.activities // Adjust package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.purpleboard.R
import com.example.purpleboard.adapters.BadgeAdapter
import com.example.purpleboard.adapters.BadgeItem
import com.example.purpleboard.adapters.ProfileDiscussionAdapter // Or DiscussionAdapter
import com.example.purpleboard.databinding.ActivityProfileBinding
import com.example.purpleboard.fragments.AvatarPickerDialogFragment
import com.example.purpleboard.models.Discussion
import com.example.purpleboard.models.User
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var prefsHelper: SharedPreferencesHelper
    private lateinit var profileDiscussionAdapter: ProfileDiscussionAdapter // Or DiscussionAdapter
    private var myDiscussionsList = mutableListOf<Discussion>()
    private lateinit var badgeAdapter: BadgeAdapter
    private var currentUserId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)
        currentUserId = prefsHelper.getUserId()

        setupToolbar()
        setupRecyclerViews()
        loadUserProfile()
        loadMyDiscussions()
        setupBadges() // Static badges for now

        binding.imageViewProfileAvatar.setOnClickListener {
            showAvatarPickerDialog()
        }

        binding.buttonPointShop.setOnClickListener {
            startActivity(Intent(this, PointShopActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh points and potentially avatar if changed in PointShop or by other means
        // For simplicity, just reloading profile which includes points.
        // A more granular update would be better in a complex app.
        loadUserProfile() // This will update points if they changed
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarProfile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.profile_title) // Set title on the toolbar
        // CollapsingToolbarLayout can also have a title that appears when collapsed
        // binding.collapsingToolbarProfile.title = getString(R.string.profile_title)
    }

    private fun setupRecyclerViews() {
        // My Discussions
        profileDiscussionAdapter = ProfileDiscussionAdapter(myDiscussionsList) { discussion ->
            val intent = Intent(this, DiscussionDetailActivity::class.java)
            intent.putExtra("DISCUSSION_ID", discussion.discussionId)
            intent.putExtra("DISCUSSION_TOPIC", discussion.topic)
            startActivity(intent)
        }
        binding.recyclerViewMyDiscussions.apply {
            layoutManager = LinearLayoutManager(this@ProfileActivity)
            adapter = profileDiscussionAdapter
            isNestedScrollingEnabled = false // Important for RecyclerView inside NestedScrollView
        }

        // Badges
        binding.recyclerViewBadges.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        // Badge adapter will be set in setupBadges()
    }

    private fun setupBadges() {
        // Static list of badges for now
        val badges = listOf(
            BadgeItem("Pioneer", R.drawable.ic_badge_placeholder_1), // Replace with actual badge drawables
            BadgeItem("Contributor", R.drawable.ic_badge_placeholder_2),
            BadgeItem("Top Student", R.drawable.ic_badge_placeholder_3)
        )
        badgeAdapter = BadgeAdapter(this, badges)
        binding.recyclerViewBadges.adapter = badgeAdapter
    }

    private fun loadUserProfile() {
        if (currentUserId == -1) {
            showToast("User not logged in.")
            // Optionally navigate to LoginActivity
            finish()
            return
        }

        binding.progressBarProfile.show()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getStudentProfile(currentUserId)
                if (response.isSuccessful && response.body() != null) {
                    updateUIWithUserData(response.body()!!)
                } else {
                    showToast("Failed to load profile: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Error loading profile: ${e.message}")
                Log.e("ProfileActivity", "Load profile exception", e)
            } finally {
                binding.progressBarProfile.hide()
            }
        }
    }

    private fun updateUIWithUserData(user: User) {
        binding.textViewProfileName.text = user.fullName
        binding.textViewProfileEmail.text = user.email
        binding.textViewMyPoints.text = "My Points: ${user.pointsCurrent}" // Show current points

        // Load avatar
        val avatarResId = resources.getIdentifier(user.avatarName, "drawable", packageName)
        if (avatarResId != 0) {
            Glide.with(this)
                .load(avatarResId)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(binding.imageViewProfileAvatar)
        } else {
            binding.imageViewProfileAvatar.setImageResource(R.drawable.ic_profile_placeholder)
        }
        // Save updated avatar name locally if it changed (e.g. fetched from server)
        prefsHelper.updateUserAvatar(user.avatarName)
    }


    private fun loadMyDiscussions() {
        if (currentUserId == -1) return

        binding.progressBarProfile.show() // Can reuse the same progress bar
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getDiscussionsByStudent(currentUserId)
                if (response.isSuccessful && response.body() != null) {
                    myDiscussionsList.clear()
                    myDiscussionsList.addAll(response.body()!!.sortedByDescending { it.createdAt })
                    profileDiscussionAdapter.updateDiscussions(myDiscussionsList)

                    if (myDiscussionsList.isEmpty()) {
                        binding.textViewNoMyDiscussions.show()
                    } else {
                        binding.textViewNoMyDiscussions.hide()
                    }
                } else {
                    showToast("Failed to load your discussions: ${response.message()}")
                    if (myDiscussionsList.isEmpty()) binding.textViewNoMyDiscussions.show()
                }
            } catch (e: Exception) {
                showToast("Error loading your discussions: ${e.message}")
                Log.e("ProfileActivity", "Load my discussions exception", e)
                if (myDiscussionsList.isEmpty()) binding.textViewNoMyDiscussions.show()
            } finally {
                binding.progressBarProfile.hide() // Ensure it's hidden after both calls
            }
        }
    }

    private fun showAvatarPickerDialog() {
        val dialog = AvatarPickerDialogFragment.newInstance()
        dialog.setOnAvatarSelectedListener { selectedAvatarName ->
            updateUserAvatarOnServer(selectedAvatarName)
        }
        dialog.show(supportFragmentManager, AvatarPickerDialogFragment.TAG)
    }

    private fun updateUserAvatarOnServer(avatarName: String) {
        if (currentUserId == -1) return

        binding.progressBarProfile.show()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.updateUserAvatar(currentUserId, mapOf("avatar_name" to avatarName))
                if (response.isSuccessful) {
                    showToast("Avatar updated successfully!")
                    // Update UI immediately and local prefs
                    val avatarResId = resources.getIdentifier(avatarName, "drawable", packageName)
                    if (avatarResId != 0) {
                        Glide.with(this@ProfileActivity).load(avatarResId).circleCrop().into(binding.imageViewProfileAvatar)
                    }
                    prefsHelper.updateUserAvatar(avatarName) // Update in SharedPreferences
                } else {
                    showToast("Failed to update avatar: ${response.errorBody()?.string() ?: response.message()}")
                }
            } catch (e: Exception) {
                showToast("Error updating avatar: ${e.message}")
                Log.e("ProfileActivity", "Update avatar exception", e)
            } finally {
                binding.progressBarProfile.hide()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}