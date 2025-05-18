package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class Discussion(
    @SerializedName("discussion_id")
    val discussionId: Int,

    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("student_name") // Name of the student who created it (e.g., "John Doe")
    val studentName: String?, // Assuming server provides this for display convenience

    @SerializedName("student_avatar") // Avatar name of the student who created it
    val studentAvatar: String?, // Assuming server provides this

    @SerializedName("topic")
    val topic: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("created_at")
    val createdAt: String, // Or use a Date/Timestamp type if you parse it

    @SerializedName("reply_count") // Optional: if your API provides the number of replies
    val replyCount: Int? = 0
)