package com.example.purpleboard.activities // Adjust package name

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purpleboard.R
import com.example.purpleboard.adapters.LeaderboardAdapter
import com.example.purpleboard.databinding.ActivityLeaderboardBinding
import com.example.purpleboard.models.LeaderboardEntry
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLeaderboardBinding
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private var leaderboardList = mutableListOf<LeaderboardEntry>()
    private var isShowingCurrentPoints = false // Default is Total Points

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeaderboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.leaderboards_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        fetchLeaderboardData() // Fetch initial (Total Points)

        binding.switchLeaderboardType.setOnCheckedChangeListener { _, isChecked ->
            isShowingCurrentPoints = isChecked
            binding.switchLeaderboardType.text = if (isChecked) getString(R.string.current_points_leaderboard) else getString(R.string.total_points_leaderboard)
            // For switch, text is for ON state. If off, it implies "Total Points" visually
            // To be more explicit, you could set the text to "Total Points" when isChecked is false.
            // However, the problem description said "switch ... will option to change table into Current Points table. The default table will be Total Points table."
            // So, the label for the switch itself might represent the state it *switches to*.
            // Let's make the switch label static "Show Current Points" and visually indicate the current table type elsewhere if needed, or rely on the user knowing.
            // Or, update the text dynamically:
            binding.switchLeaderboardType.text = if (isChecked) "Showing: Current Points" else "Showing: Total Points" // Or use resource strings
            fetchLeaderboardData()
        }
        // Initial switch text
        binding.switchLeaderboardType.text = if (isShowingCurrentPoints) "Showing: Current Points" else "Showing: Total Points"

    }

    private fun setupRecyclerView() {
        leaderboardAdapter = LeaderboardAdapter(leaderboardList)
        binding.recyclerViewLeaderboard.apply {
            layoutManager = LinearLayoutManager(this@LeaderboardActivity)
            adapter = leaderboardAdapter
        }
    }

    private fun fetchLeaderboardData() {
        showLoading(true)
        binding.textViewNoLeaderboardData.hide()

        lifecycleScope.launch {
            try {
                val response = if (isShowingCurrentPoints) {
                    RetrofitClient.apiService.getLeaderboardCurrent()
                } else {
                    RetrofitClient.apiService.getLeaderboardTotal()
                }

                if (response.isSuccessful && response.body() != null) {
                    val fetchedEntries = response.body()!!
                    // Server should ideally return sorted data. If not, sort here:
                    // val sortedEntries = fetchedEntries.sortedByDescending { it.points }
                    leaderboardList.clear()
                    leaderboardList.addAll(fetchedEntries) // Assuming server sends sorted
                    leaderboardAdapter.updateLeaderboard(leaderboardList)

                    if (leaderboardList.isEmpty()) {
                        binding.textViewNoLeaderboardData.show()
                    } else {
                        binding.textViewNoLeaderboardData.hide()
                    }
                } else {
                    showToast("Failed to load leaderboard: ${response.message()}")
                    Log.e("LeaderboardActivity", "API Error: ${response.code()} - ${response.message()}")
                    if (leaderboardList.isEmpty()) binding.textViewNoLeaderboardData.show()
                }
            } catch (e: Exception) {
                showToast("Error fetching leaderboard: ${e.message}")
                Log.e("LeaderboardActivity", "Exception: ", e)
                if (leaderboardList.isEmpty()) binding.textViewNoLeaderboardData.show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarLeaderboard.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewLeaderboard.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}