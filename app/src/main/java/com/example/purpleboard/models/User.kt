package com.example.purpleboard.models // Adjust if your package structure is different

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("student_id") // Matches the key from your Flask API response for login/registration
    val studentId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("email")
    val email: String,

    // Password is not typically sent back from the server after login/registration for security
    // It's used for requests only.

    @SerializedName("points_total")
    val pointsTotal: Int,

    @SerializedName("points_current")
    val pointsCurrent: Int,

    @SerializedName("avatar_name")
    val avatarName: String, // e.g., "avatar1", "avatar5"

    @SerializedName("created_at")
    val createdAt: String? = null // Optional, depends if your API sends it
) {
    // Computed property to get the full name
    val fullName: String
        get() = "$name $surname"
}

// Data class for login response, which might just include the User object and a message
data class LoginResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("user") // Assuming your server nests the user object under 'user' key
    val user: User?, // User can be null if login fails with just a message
    @SerializedName("error")
    val error: String? = null // For error messages
)

// Data class for registration response
data class RegisterResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("student_id") // Or 'user_id'
    val studentId: Int?, // Can be null if registration fails
    @SerializedName("user") // If server sends back the full user object on successful registration
    val user: User? = null,
    @SerializedName("error")
    val error: String? = null // For error messages
)

// A generic API response for simple messages (e.g., success/failure of an update)
data class SimpleApiResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("error")
    val error: String? = null
)