package com.example.purpleboard.activities // Adjust package name

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View // Ensure this is imported
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.purpleboard.R
import com.example.purpleboard.databinding.ActivityRegisterBinding
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
    // private lateinit var prefsHelper: SharedPreferencesHelper // prefsHelper not used in this version for registration logic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // prefsHelper = SharedPreferencesHelper(this) // Not needed here for now

        setupValidationListeners()

        binding.buttonRegister.setOnClickListener {
            performRegistration()
        }
    }

    private fun setupValidationListeners() {
        binding.editTextName.doAfterTextChanged { validateName() }
        binding.editTextSurname.doAfterTextChanged { validateSurname() }
        binding.editTextEmailRegister.doAfterTextChanged { validateEmail() }
        binding.editTextPasswordRegister.doAfterTextChanged {
            validatePassword()
            validateConfirmPassword() // Re-validate confirm password if main password changes
        }
        binding.editTextConfirmPassword.doAfterTextChanged {
            validateConfirmPassword()
        }
    }

    private fun validateName(): Boolean {
        val name = binding.editTextName.text.toString().trim()
        if (Validators.isNameValid(name)) {
            binding.tilName.error = null
            binding.tilName.isErrorEnabled = false // <<< ADD THIS
            return true
        } else {
            binding.tilName.error = "Name is required (min 2 chars)"
            return false
        }
    }

    private fun validateSurname(): Boolean {
        val surname = binding.editTextSurname.text.toString().trim()
        if (Validators.isNameValid(surname)) {
            binding.tilSurname.error = null
            binding.tilSurname.isErrorEnabled = false // <<< ADD THIS
            return true
        } else {
            binding.tilSurname.error = "Surname is required (min 2 chars)"
            return false
        }
    }

    private fun validateEmail(): Boolean {
        val email = binding.editTextEmailRegister.text.toString().trim()
        if (Validators.isValidEmail(email)) {
            binding.tilEmailRegister.error = null
            binding.tilEmailRegister.isErrorEnabled = false // <<< ADD THIS
            return true
        } else {
            binding.tilEmailRegister.error = "Invalid email format"
            return false
        }
    }


    private fun validatePassword(): Boolean {
        val password = binding.editTextPasswordRegister.text.toString()
        if (Validators.isValidPassword(password)) {
            binding.tilPasswordRegister.error = null
            binding.tilPasswordRegister.isErrorEnabled = false // <<< ADD THIS to remove space when valid
            // If you were using the separate TextView for rules:
            // binding.textViewPasswordRules.hide()
            return true
        } else {
            binding.tilPasswordRegister.error = getString(R.string.password_rules)
            // If you were using the separate TextView for rules:
            // binding.textViewPasswordRules.show()
            return false
        }
    }

    private fun validateConfirmPassword(): Boolean {
        val password = binding.editTextPasswordRegister.text.toString()
        val confirmPassword = binding.editTextConfirmPassword.text.toString()
        // Only show mismatch error if confirm password field is not empty and password has some text
        if (confirmPassword.isNotEmpty() && !Validators.doPasswordsMatch(password, confirmPassword)) {
            binding.tilConfirmPassword.error = getString(R.string.password_mismatch)
            // If you were using the separate TextView for mismatch error:
            // binding.textViewConfirmPasswordError.show()
            return false
        } else {
            binding.tilConfirmPassword.error = null
            binding.tilConfirmPassword.isErrorEnabled = false // <<< ADD THIS to remove space when valid or empty
            // If you were using the separate TextView for mismatch error:
            // binding.textViewConfirmPasswordError.hide()
            return true
        }
    }


    private fun performRegistration() {
        // Call individual validation functions which now handle UI updates (setting/clearing errors)
        val isNameValid = validateName()
        val isSurnameValid = validateSurname()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val doPasswordsMatch = validateConfirmPassword() // This also checks if confirm field is empty correctly

        if (!isNameValid || !isSurnameValid || !isEmailValid || !isPasswordValid || !doPasswordsMatch) {
            showToast("Please correct the errors above.")
            return
        }

        // All fields are valid if we reach here
        val name = binding.editTextName.text.toString().trim()
        val surname = binding.editTextSurname.text.toString().trim()
        val email = binding.editTextEmailRegister.text.toString().trim()
        val password = binding.editTextPasswordRegister.text.toString()

        showLoading(true)

        lifecycleScope.launch {
            try {
                val registerRequest = RegisterRequest(name, surname, email, password)
                val response = RetrofitClient.apiService.registerUser(registerRequest)

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    if (registerResponse.studentId != null || registerResponse.user != null) {
                        showToast("Registration successful! Please login.")
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        showToast(registerResponse.message ?: registerResponse.error ?: "Registration failed")
                        Log.e("RegisterActivity", "Registration failed: ${registerResponse.message ?: registerResponse.error}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown registration error"
                    // Check if errorBody contains specific messages for already existing email
                    if (errorBody.contains("Email already exists", ignoreCase = true)) {
                        binding.tilEmailRegister.error = "This email is already registered."
                        showToast("This email is already registered.")
                    } else {
                        showToast("Registration API error: $errorBody")
                    }
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
        binding.progressBarRegister.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !isLoading
    }

    // override fun onSupportNavigateUp(): Boolean { ... } // If you add a toolbar
}