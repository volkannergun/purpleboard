package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class Reply(
    @SerializedName("reply_id")
    val replyId: Int,

    @SerializedName("discussion_id")
    val discussionId: Int,

    @SerializedName("student_id")
    val studentId: Int,

    @SerializedName("student_name") // Name of the student who replied
    val studentName: String?, // Assuming server provides this

    @SerializedName("student_avatar") // Avatar name of the student who replied
    val studentAvatar: String?, // Assuming server provides this

    @SerializedName("reply_text")
    val replyText: String,

    @SerializedName("created_at")
    val createdAt: String // Or use a Date/Timestamp type
)