package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class Assignment(
    @SerializedName("assignment_id")
    val assignmentId: Int,

    @SerializedName("topic")
    val topic: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("points_value")
    val pointsValue: Int,

    @SerializedName("created_at")
    val createdAt: String, // Or use a Date/Timestamp type

    // This field is not from the server's main assignment list,
    // but we'll use it in the app to track completion status for the UI.
    // It will be populated client-side.
    @Transient // Tells Gson to ignore this field during serialization/deserialization
    var isCompletedByCurrentUser: Boolean = false
)