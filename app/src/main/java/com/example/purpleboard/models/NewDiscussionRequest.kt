package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class NewDiscussionRequest(
    // student_id will be passed as part of the authenticated request (e.g., in headers or path by the server)
    // or you might need to include it in the body if your API is designed that way.
    // For now, let's assume the server gets it from the session/token.
    // If you need to send it from the client, add:
    // @SerializedName("student_id")
    // val studentId: Int,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("description")
    val description: String
)