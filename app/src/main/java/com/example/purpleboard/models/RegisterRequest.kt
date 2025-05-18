package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("surname")
    val surname: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
    // Confirm password is usually handled client-side and not sent to the server
)