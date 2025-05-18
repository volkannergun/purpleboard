package com.example.purpleboard.activities // Adjust package name

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityHomeBinding // Generated ViewBinding class
import com.example.purpleboard.utils.SharedPreferencesHelper

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        // Set ActionBar title - HomeActivity uses Theme.Purpleboard.WithActionBar
        supportActionBar?.title = getString(R.string.home_title)

        val userName = prefsHelper.getUserName()
        if (!userName.isNullOrEmpty()) {
            binding.textViewWelcome.text = "Welcome, $userName!"
        } else {
            binding.textViewWelcome.text = "Welcome!"
        }


        binding.buttonDiscussions.setOnClickListener {
            startActivity(Intent(this, DiscussionsActivity::class.java))
        }

        binding.buttonAssignments.setOnClickListener {
            startActivity(Intent(this, AssignmentsActivity::class.java))
        }

        binding.buttonLeaderboards.setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.buttonLogout.setOnClickListener {
            prefsHelper.clearSession()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}