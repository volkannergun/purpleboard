package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class LeaderboardEntry(
    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("name") // Full name: "Name Surname"
    val name: String,

    @SerializedName("avatar_name")
    val avatarName: String,

    @SerializedName("points") // This will be either points_total or points_current depending on the API endpoint
    val points: Int,

    @SerializedName("rank") // Optional: if your API provides the rank
    val rank: Int? = null
)