package com.example.purpleboard.activities // Adjust package name

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View // Make sure this import is present
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

    // ActivityResultLauncher for when an assignment is completed OR a new one is added
    private val assignmentModificationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // An assignment was completed OR a new one was created
                // Refresh the list to show changes (green background or new item)
                val completedId = result.data?.getIntExtra("COMPLETED_ASSIGNMENT_ID", -1) ?: -1
                val newAssignmentAdded = result.data?.getBooleanExtra("NEW_ASSIGNMENT_ADDED", false) ?: false

                if (completedId != -1) {
                    completedAssignmentIds.add(completedId)
                }
                // Always refresh if OK, as either completion or addition happened
                fetchAssignments()
            }
        }
    // Note: We're using one launcher for both detail (completion) and new assignment creation.
    // If NewAssignmentActivity sets RESULT_OK, this will trigger a refresh.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAssignmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        supportActionBar?.title = getString(R.string.assignments_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupRecyclerView()
        setupFab() // Call method to setup FAB
        fetchAssignments() // Initial fetch
    }

    override fun onResume() {
        super.onResume()
        // If coming back to this screen, it's good practice to refresh data
        // if there's a chance it might have changed from another part of the app
        // or if the ActivityResultLauncher isn't solely relied upon.
        // For this app, fetchAssignments() on launcher result is likely sufficient.
        // However, if an admin creates an assignment on another device, this wouldn't auto-update
        // without more advanced real-time updates or pull-to-refresh.
        // For now, we rely on the result launcher.
    }

    private fun setupFab() {
        if (prefsHelper.getUserRole() == "admin") {
            binding.fabAddAssignment.show() // Use the extension function
            binding.fabAddAssignment.setOnClickListener {
                val intent = Intent(this, NewAssignmentActivity::class.java)
                // Use the same launcher, NewAssignmentActivity will set RESULT_OK
                assignmentModificationLauncher.launch(intent)
            }
        } else {
            binding.fabAddAssignment.hide() // Use the extension function
        }
    }

    private fun setupRecyclerView() {
        assignmentAdapter = AssignmentAdapter(assignmentList) { assignment ->
            val intent = Intent(this, AssignmentDetailActivity::class.java)
            intent.putExtra("ASSIGNMENT_ID", assignment.assignmentId)
            intent.putExtra("ASSIGNMENT_TOPIC", assignment.topic)
            intent.putExtra("IS_COMPLETED", assignment.isCompletedByCurrentUser)
            // Use the same launcher for detail activity
            assignmentModificationLauncher.launch(intent)
        }
        binding.recyclerViewAssignments.apply {
            layoutManager = LinearLayoutManager(this@AssignmentsActivity)
            adapter = assignmentAdapter
        }
    }

    // ... (inside fetchAssignments method)
    private fun fetchAssignments() {
        showLoading(true)
        binding.textViewNoAssignments.hide()
        val currentLoggedInStudentId = prefsHelper.getUserId() // ID of the user currently logged in

        lifecycleScope.launch {
            var allAssignments: List<Assignment>? = null
            // completedAssignmentIds is a class member set, will be updated

            try {
                val assignmentsResponse = RetrofitClient.apiService.getAllAssignments()
                if (assignmentsResponse.isSuccessful) {
                    allAssignments = assignmentsResponse.body()
                } else {
                    showToast("Failed to load assignments: ${assignmentsResponse.message()}")
                    Log.e("AssignmentsActivity", "Assignments API Error: ${assignmentsResponse.code()} - ${assignmentsResponse.message()}")
                }

                if (currentLoggedInStudentId != -1) {
                    // Pass currentLoggedInStudentId for both path and header
                    val completedResponse = RetrofitClient.apiService.getCompletedAssignmentIds(
                        currentLoggedInStudentId, // studentIdInPath (for the URL)
                        currentLoggedInStudentId  // authenticatedStudentId (for the X-Student-ID header)
                    )
                    if (completedResponse.isSuccessful && completedResponse.body() != null) {
                        completedAssignmentIds.clear()
                        completedAssignmentIds.addAll(completedResponse.body()!!)
                        Log.d("AssignmentsActivity", "Fetched completed IDs: $completedAssignmentIds") // For debugging
                    } else {
                        Log.w("AssignmentsActivity", "Could not fetch completed assignment IDs: ${completedResponse.code()} - ${completedResponse.message()}")
                        // If this call fails, completedAssignmentIds might be empty or stale,
                        // leading to assignments appearing uncompleted.
                    }
                } else {
                    // Handle case where user ID is not available (e.g., not logged in properly)
                    // Maybe clear completedAssignmentIds or show an error
                    completedAssignmentIds.clear()
                    Log.w("AssignmentsActivity", "User ID not found, cannot fetch completed assignments.")
                }

                if (allAssignments != null) {
                    val processedAssignments = allAssignments.map { assignment ->
                        assignment.copy(isCompletedByCurrentUser = completedAssignmentIds.contains(assignment.assignmentId))
                    }
                    assignmentList.clear()
                    assignmentList.addAll(processedAssignments.sortedByDescending { it.createdAt })
                    assignmentAdapter.updateAssignments(assignmentList)

                    binding.textViewNoAssignments.visibility = if (assignmentList.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    binding.textViewNoAssignments.visibility = if (assignmentList.isEmpty()) View.VISIBLE else View.GONE
                }

            } catch (e: Exception) {
                showToast("Error fetching assignment data: ${e.message}")
                Log.e("AssignmentsActivity", "Exception: ", e)
                binding.textViewNoAssignments.visibility = if (assignmentList.isEmpty()) View.VISIBLE else View.GONE
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        // Ensure View import is present: import android.view.View
        binding.progressBarAssignments.visibility = if (isLoading) View.VISIBLE else View.GONE
        // Only hide RecyclerView if loading AND list is currently empty to avoid flicker
        if (isLoading) {
            binding.recyclerViewAssignments.visibility = View.GONE
        } else {
            binding.recyclerViewAssignments.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}