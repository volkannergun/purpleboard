package com.example.purpleboard.utils // Adjust package name

import android.content.Context
import android.content.SharedPreferences
import com.example.purpleboard.models.User
import com.google.gson.Gson

class SharedPreferencesHelper(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    fun saveLoginSession(user: User) {
        editor.putInt(Constants.KEY_USER_ID, user.studentId)
        editor.putString(Constants.KEY_USER_EMAIL, user.email)
        editor.putString(Constants.KEY_USER_NAME, user.fullName) // Save "Name Surname"
        editor.putString(Constants.KEY_USER_AVATAR, user.avatarName)
        editor.putString(Constants.KEY_USER_ROLE, user.userRole)
        editor.putBoolean(Constants.KEY_IS_LOGGED_IN, true)
        // You could also save the entire User object as JSON if needed,
        // but individual fields are often fine for quick access.
        // editor.putString("user_object", gson.toJson(user))
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(Constants.KEY_USER_ID, -1) // Return -1 or throw exception if not found
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_EMAIL, null)
    }

    fun getUserName(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_NAME, null)
    }

    fun getUserAvatar(): String? {
        return sharedPreferences.getString(Constants.KEY_USER_AVATAR, "avatar1") // Default avatar
    }

    fun updateUserAvatar(avatarName: String) {
        editor.putString(Constants.KEY_USER_AVATAR, avatarName)
        editor.apply()
    }

    fun getUserRole(): String { // Return non-nullable, default to "student"
        return sharedPreferences.getString(Constants.KEY_USER_ROLE, "student") ?: "student"
    }

    fun clearSession() {
        editor.remove(Constants.KEY_USER_ID)
        editor.remove(Constants.KEY_USER_EMAIL)
        editor.remove(Constants.KEY_USER_NAME)
        editor.remove(Constants.KEY_USER_AVATAR)
        editor.remove(Constants.KEY_USER_ROLE)
        editor.remove(Constants.KEY_IS_LOGGED_IN)
        // editor.remove("user_object")
        editor.apply()
    }

    // Example of saving a simple string preference
    fun saveString(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    // Add other getters/setters for different data types as needed (Int, Boolean, etc.)
}