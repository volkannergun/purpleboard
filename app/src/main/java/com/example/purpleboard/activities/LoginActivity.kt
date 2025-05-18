package com.example.purpleboard.activities // Adjust package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.databinding.ActivityLoginBinding // Generated ViewBinding class
import com.example.purpleboard.models.LoginRequest
import com.example.purpleboard.models.User
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.Validators
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefsHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        // Check if user is already logged in
        if (prefsHelper.isLoggedIn()) {
            navigateToHome()
            return // Finish this activity if already logged in
        }

        // Set the ActionBar title (if your theme for LoginActivity includes an ActionBar)
        // As per manifest, LoginActivity uses Theme.Purpleboard (NoActionBar), so this line might not be needed
        // or you might want to add a Toolbar manually in the XML and set its title.
        // For now, assuming no visible action bar.
        // supportActionBar?.title = getString(R.string.login_title)


        binding.buttonLogin.setOnClickListener {
            performLogin()
        }

        binding.buttonGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun performLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString()

        // Basic Client-Side Validation
        if (!Validators.isValidEmail(email)) {
            binding.tilEmail.error = "Invalid email format"
            return
        } else {
            binding.tilEmail.error = null // Clear error
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = "Password cannot be empty"
            return
        } else {
            binding.tilPassword.error = null // Clear error
        }

        showLoading(true)

        lifecycleScope.launch { // Launch a coroutine
            try {
                val loginRequest = LoginRequest(email, password)
                val response = RetrofitClient.apiService.loginUser(loginRequest)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.user != null) {
                        // Login successful
                        prefsHelper.saveLoginSession(loginResponse.user)
                        showToast("Login successful! Welcome ${loginResponse.user.name}")
                        navigateToHome()
                    } else {
                        // Login failed, server returned success but no user (e.g., invalid credentials with specific message)
                        showToast(loginResponse.message ?: loginResponse.error ?: "Invalid credentials")
                        Log.e("LoginActivity", "Login failed: ${loginResponse.message ?: loginResponse.error}")
                    }
                } else {
                    // API call failed (e.g., 401, 404, 500)
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    showToast("Login failed: $errorBody")
                    Log.e("LoginActivity", "Login API error: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                // Network error or other exception
                showToast("Login error: ${e.message}")
                Log.e("LoginActivity", "Exception during login", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
        startActivity(intent)
        finish() // Finish LoginActivity
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarLogin.show()
            binding.buttonLogin.isEnabled = false
            binding.buttonGoToRegister.isEnabled = false
        } else {
            binding.progressBarLogin.hide()
            binding.buttonLogin.isEnabled = true
            binding.buttonGoToRegister.isEnabled = true
        }
    }
}