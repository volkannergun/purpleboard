package com.example.purpleboard.models // Adjust package name

import com.google.gson.annotations.SerializedName

data class NewReplyRequest(
    // discussion_id will likely be part of the URL path (e.g., /api/discussions/{discussion_id}/replies)
    // student_id will be identified by the server from the authenticated session/token.
    // If you need to send them in the body, add them here.

    @SerializedName("reply_text")
    val replyText: String
)