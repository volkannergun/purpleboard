package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.purpleboard.R
import com.example.purpleboard.adapters.AssignmentAdapter
import com.example.purpleboard.databinding.ActivityAssignmentsBinding
import com.example.purpleboard.models.Assignment
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class AssignmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAssignmentsBinding
    private lateinit var assignmentAdapter: AssignmentAdapter
    private var assignmentList = mutableListOf<Assignment>()
    private lateinit var prefsHelper: SharedPreferencesHelper
    private var completedAssignmentIds = mutableSetOf<Int>()

    // ActivityResultLauncher for when an assignment is completed
    private val assignmentDetailResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // An assignment was completed, refresh the list to show green background
                val completedId = result.data?.getIntExtra("COMPLETED_ASSIGNMENT_ID", -1) ?: -1
                if (completedId != -1) {
                    completedAssignmentIds.add(completedId)
                    // Update the specific item or refresh all for simplicity here
                    fetchAssignments() // Easiest way to reflect changes
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = getString(R.string.assignments_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        fetchAssignments() // Initial fetch
    }

    override fun onResume() {
        super.onResume()
        // If coming back from AssignmentDetailActivity after a completion,
        // fetchAssignments might be called by the resultLauncher.
        // If not using resultLauncher for some reason, you might refresh here.
    }


    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(assignmentList) { assignment ->
            val intent = Intent(this, AssignmentDetailActivity::class.java)
            intent.putExtra("ASSIGNMENT_ID", assignment.assignmentId)
            intent.putExtra("ASSIGNMENT_TOPIC", assignment.topic)
            intent.putExtra("IS_COMPLETED", assignment.isCompletedByCurrentUser)
            assignmentDetailResultLauncher.launch(intent)
        }
        binding.recyclerViewAssignments.apply {
            layoutManager = LinearLayoutManager(this@AssignmentsActivity)
            adapter = assignmentAdapter
        }
    }

    private fun fetchAssignments() {
        showLoading(true)
        binding.textViewNoAssignments.hide()
        val studentId = prefsHelper.getUserId()

        lifecycleScope.launch {
            // Step 1: Fetch all assignments
            // Step 2: Fetch completed assignment IDs for the current user
            // Step 3: Merge this information before updating the adapter
            var allAssignments: List<Assignment>? = null
            var fetchedCompletedIds: List<Int>? = null

            try {
                val assignmentsResponse = RetrofitClient.apiService.getAllAssignments()
                if (assignmentsResponse.isSuccessful) {
                    allAssignments = assignmentsResponse.body()
                } else {
                    showToast("Failed to load assignments: ${assignmentsResponse.message()}")
                    Log.e("AssignmentsActivity", "Assignments API Error: ${assignmentsResponse.code()} - ${assignmentsResponse.message()}")
                }

                if (studentId != -1) {
                    val completedResponse = RetrofitClient.apiService.getCompletedAssignmentIds(studentId)
                    if (completedResponse.isSuccessful) {
                        fetchedCompletedIds = completedResponse.body()
                        // Update our local set of completed IDs
                        completedAssignmentIds.clear()
                        fetchedCompletedIds?.let { completedAssignmentIds.addAll(it) }
                    } else {
                        Log.e("AssignmentsActivity", "Completed IDs API Error: ${completedResponse.code()} - ${completedResponse.message()}")
                        // Continue without completed info if this fails, or show specific error
                    }
                }

                if (allAssignments != null) {
                    val processedAssignments = allAssignments.map { assignment ->
                        assignment.copy(isCompletedByCurrentUser = completedAssignmentIds.contains(assignment.assignmentId))
                    }
                    assignmentList.clear()
                    // Assignments usually aren't sorted by date, but by a specific order if any
                    assignmentList.addAll(processedAssignments)
                    assignmentAdapter.updateAssignments(assignmentList)

                    if (assignmentList.isEmpty()) {
                        binding.textViewNoAssignments.show()
                    } else {
                        binding.textViewNoAssignments.hide()
                    }
                } else {
                    if (assignmentList.isEmpty()) binding.textViewNoAssignments.show()
                }

            } catch (e: Exception) {
                showToast("Error fetching assignment data: ${e.message}")
                Log.e("AssignmentsActivity", "Exception: ", e)
                if (assignmentList.isEmpty()) binding.textViewNoAssignments.show()
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarAssignments.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewAssignments.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}