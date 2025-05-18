package com.example.purpleboard.network // Adjust package name

import com.example.purpleboard.models.*
import retrofit2.Response // Important: Use retrofit2.Response for full HTTP response access
import retrofit2.http.*

interface ApiService {

    // --- Authentication ---
    @POST("register") // Endpoint relative to BASE_URL (e.g., api/register)
    suspend fun registerUser(@Body registerRequest: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): Response<LoginResponse>

    // --- Student/Profile ---
    @GET("students/{student_id}")
    suspend fun getStudentProfile(@Path("student_id") studentId: Int): Response<User> // Assuming it returns a User object

    @PUT("students/{student_id}/avatar")
    suspend fun updateUserAvatar(
        @Path("student_id") studentId: Int,
        @Body avatarUpdateRequest: Map<String, String> // e.g., mapOf("avatar_name" to "avatar3")
    ): Response<SimpleApiResponse> // Or a response with updated user data

    // --- Discussions ---
    @GET("discussions")
    suspend fun getAllDiscussions(@Query("student_id") currentUserId: Int? = null): Response<List<Discussion>> // Pass currentUserId to mark user's own posts, if API supports

    @POST("discussions")
    suspend fun createDiscussion(
        @Header("X-Student-ID") studentId: Int, // Example of sending student ID in header
        @Body newDiscussionRequest: NewDiscussionRequest
    ): Response<Discussion> // Assuming server returns the created discussion with its ID

    @GET("discussions/{discussion_id}")
    suspend fun getDiscussionDetails(@Path("discussion_id") discussionId: Int): Response<Discussion> // Assuming this returns the discussion details without replies initially

    @GET("discussions/{discussion_id}/replies")
    suspend fun getDiscussionReplies(@Path("discussion_id") discussionId: Int): Response<List<Reply>>

    @POST("discussions/{discussion_id}/replies")
    suspend fun postReply(
        @Path("discussion_id") discussionId: Int,
        @Header("X-Student-ID") studentId: Int, // Example
        @Body newReplyRequest: NewReplyRequest
    ): Response<Reply> // Assuming server returns the created reply

    @GET("students/{student_id}/discussions")
    suspend fun getDiscussionsByStudent(@Path("student_id") studentId: Int): Response<List<Discussion>>


    // --- Assignments ---
    @GET("assignments")
    suspend fun getAllAssignments(): Response<List<Assignment>>

    @GET("assignments/{assignment_id}")
    suspend fun getAssignmentDetails(@Path("assignment_id") assignmentId: Int): Response<Assignment>

    @POST("assignments/{assignment_id}/complete")
    suspend fun completeAssignment(
        @Path("assignment_id") assignmentId: Int,
        @Header("X-Student-ID") studentId: Int, // Example
        @Body submitAssignmentRequest: SubmitAssignmentRequest
    ): Response<SimpleApiResponse> // Or response with updated points

    @GET("students/{student_id}/assignments/completed")
    suspend fun getCompletedAssignmentIds(@Path("student_id") studentId: Int): Response<List<Int>> // Returns list of assignment_ids


    // --- Leaderboard ---
    @GET("leaderboard/total")
    suspend fun getLeaderboardTotal(): Response<List<LeaderboardEntry>>

    @GET("leaderboard/current")
    suspend fun getLeaderboardCurrent(): Response<List<LeaderboardEntry>>


    // --- Shop ---
    @GET("shop/items")
    suspend fun getShopItems(): Response<List<ShopItem>>

    @POST("shop/purchase/{item_id}")
    suspend fun purchaseShopItem(
        @Path("item_id") itemId: Int,
        @Header("X-Student-ID") studentId: Int // Example
    ): Response<SimpleApiResponse> // Or response with updated points and item status

    @GET("students/{student_id}/purchases") // To check if student has bought an item
    suspend fun getStudentPurchases(@Path("student_id") studentId: Int): Response<List<Int>> // Returns list of item_ids
}