package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class SubmitAssignmentRequest(
    // assignment_id will likely be part of the URL path (e.g., /api/assignments/{assignment_id}/complete)
    // student_id will be identified by the server.

    @SerializedName("submitted_reply") // The content the student submits for the assignment
    val submittedReply: String
)

// It's also good to have a model for the response when completing an assignment,
// especially if it returns the updated point totals or a confirmation.
// We can reuse SimpleApiResponse or create a specific one if needed.
// For now, let's assume SimpleApiResponse is sufficient or the server just returns 200 OK.