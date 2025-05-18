package com.example.purpleboard.activities // Adjust package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityRegisterBinding // Generated ViewBinding class
import com.example.purpleboard.models.RegisterRequest
import com.example.purpleboard.network.RetrofitClient
import com.example.purpleboard.utils.SharedPreferencesHelper
import com.example.purpleboard.utils.Validators
import com.example.purpleboard.utils.hide
import com.example.purpleboard.utils.show
import com.example.purpleboard.utils.showToast
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var prefsHelper: SharedPreferencesHelper // To auto-login after successful registration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefsHelper = SharedPreferencesHelper(this)

        // As per manifest, RegisterActivity uses Theme.Purpleboard (NoActionBar)
        // If you add a Toolbar manually and want Up navigation:
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // supportActionBar?.title = getString(R.string.register_title)

        setupValidationListeners()

        binding.buttonRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun setupValidationListeners() {
        binding.editTextPasswordRegister.doAfterTextChanged {
            validatePassword()
        }
        binding.editTextConfirmPassword.doAfterTextChanged {
            validateConfirmPassword()
        }
    }

    private fun validatePassword(): Boolean {
        val password = binding.editTextPasswordRegister.text.toString()
        if (Validators.isValidPassword(password)) {
            binding.textViewPasswordRules.hide()
            binding.tilPasswordRegister.error = null
            return true
        } else {
            binding.textViewPasswordRules.show()
            binding.tilPasswordRegister.error = getString(R.string.password_rules) // You can also set error on TIL
            return false
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = binding.editTextPasswordRegister.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        if (Validators.doPasswordsMatch(password, confirmPassword)) {
            binding.textViewConfirmPasswordError.hide()
            binding.tilConfirmPassword.error = null
            return true
        } else {
            binding.textViewConfirmPasswordError.show()
            binding.tilConfirmPassword.error = getString(R.string.password_mismatch)
            return false
        }
    }


    private fun performRegistration() {
        val name = binding.editTextName.text.toString().trim()
        val surname = binding.editTextSurname.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val password = binding.editTextPasswordRegister.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()

        // Client-Side Validation
        var isValid = true
        if (!Validators.isNameValid(name)) {
            binding.tilName.error = "Name is required (min 2 chars)"
            isValid = false
        } else {
            binding.tilName.error = null
        }

        if (!Validators.isNameValid(surname)) {
            binding.tilSurname.error = "Surname is required (min 2 chars)"
            isValid = false
        } else {
            binding.tilSurname.error = null
        }

        if (!Validators.isValidEmail(email)) {
            binding.tilEmailRegister.error = "Invalid email format"
            isValid = false
        } else {
            binding.tilEmailRegister.error = null
        }

        if (!validatePassword()) isValid = false // Checks rules and shows/hides red text
        if (!validateConfirmPassword()) isValid = false // Checks match and shows/hides red text

        if (!isValid) {
            showToast("Please correct the errors.")
            return
        }

        showLoading(true)

        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(name, surname, email, password)
                val response = RetrofitClient.apiService.registerUser(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    if (registerResponse.studentId != null || registerResponse.user != null) {
                        showToast("Registration successful! Please login.")
                        // Option 1: Navigate to Login
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()

                        // Option 2: Auto-login (if server returns user object and it's desired)
                        /*
                        if (registerResponse.user != null) {
                            prefsHelper.saveLoginSession(registerResponse.user)
                            showToast("Registration successful! Welcome ${registerResponse.user.name}")
                            val homeIntent = Intent(this@RegisterActivity, HomeActivity::class.java)
                            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(homeIntent)
                            finishAffinity() // Finishes this and any parent login activities
                        } else {
                            // Fallback to login if user object not returned
                            val loginIntent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            loginIntent.putExtra("email_for_login", email) // Pre-fill email on login screen
                            startActivity(loginIntent)
                            finish()
                        }
                        */
                    } else {
                        showToast(registerResponse.message ?: registerResponse.error ?: "Registration failed")
                        Log.e("RegisterActivity", "Registration failed: ${registerResponse.message ?: registerResponse.error}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown registration error"
                    showToast("Registration API error: $errorBody")
                    Log.e("RegisterActivity", "Registration API error: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                showToast("Registration error: ${e.message}")
                Log.e("RegisterActivity", "Exception during registration", e)
            } finally {
                showLoading(false)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarRegister.show()
            binding.buttonRegister.isEnabled = false
        } else {
            binding.progressBarRegister.hide()
            binding.buttonRegister.isEnabled = true
        }
    }

    // For Up navigation if you add a Toolbar and setDisplayHomeAsUpEnabled(true)
    // override fun onSupportNavigateUp(): Boolean {
    //     onBackPressedDispatcher.onBackPressed()
    //     return true
    // }
}