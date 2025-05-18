package com.example.purpleboard.utils // Adjust package name

import android.util.Patterns
import java.util.regex.Pattern

object Validators {

    // Password rules: 6-12 characters, lowercase, uppercase, number
    private val PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,12}$"
    )

    fun isValidEmail(email: String): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return !password.isNullOrBlank() && PASSWORD_PATTERN.matcher(password).matches()
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }

    fun isNameValid(name: String?): Boolean {
        // Basic check: not null or empty, and maybe a minimum length
        return !name.isNullOrBlank() && name.trim().length >= 2
    }
}