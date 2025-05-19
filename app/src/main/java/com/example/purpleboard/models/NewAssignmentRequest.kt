package com.example.purpleboard.models // Adjust package

import com.google.gson.annotations.SerializedName

data class NewAssignmentRequest(
    @SerializedName("topic")
    val topic: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("points_value")
    val pointsValue: Int
)