package com.example.purpleboard.utils // Adjust if your package structure is different

object Constants {

    // Flask Server Base URL - Replace with your actual IP and port
    // Ensure your Android device/emulator can reach this IP address.
    // If using emulator and server is on localhost of PC: http://10.0.2.2:5000/
    // If using physical device on same Wi-Fi: http://YOUR_PC_WIFI_IP:5000/
    const val BASE_URL = "http://192.168.0.27:5000/api/" // Added /api/ suffix as planned

    // SharedPreferences Keys
    const val PREFS_NAME = "purpleboard_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_USER_NAME = "user_name" // For "Name Surname"
    const val KEY_USER_AVATAR = "user_avatar"
    const val KEY_USER_ROLE = "user_role"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
    // Add other keys as needed, e.g., for a session token if your server provides one

    // Request Codes or other constants
    // const val SOME_REQUEST_CODE = 101

    // Point values - could also be fetched from server if they are dynamic
    const val POINTS_CREATE_DISCUSSION = 3
    const val POINTS_SEND_REPLY = 2
    const val POINTS_COMPLETE_ASSIGNMENT = 5

}