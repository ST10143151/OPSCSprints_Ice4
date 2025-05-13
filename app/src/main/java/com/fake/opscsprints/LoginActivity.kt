// LoginActivity.kt
package com.fake.opscsprints

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fake.opscsprints.databinding.ActivityLoginBinding
// Firebase temporarily disabled
// import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.auth.ktx.auth
// import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    // Mock Firebase auth for temporary build fix
    // private lateinit var auth: FirebaseAuth
    private val mockAuth = true // Temporary mock
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase auth disabled temporarily
        // auth = Firebase.auth

        // Auto-login disabled while Firebase is disabled
        // if (auth.currentUser != null && auth.currentUser!!.isEmailVerified) {
        //     navigateToMain()
        // }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (validateInput(email, password)) {
                performLogin(email, password)
            }
        }

        binding.registerLink.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        binding.forgotPasswordLink.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            if (email.isNotEmpty()) {
                // Firebase auth disabled temporarily
                // auth.sendPasswordResetEmail(email)
                //    .addOnSuccessListener {
                //        Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show()
                //    }
                //    .addOnFailureListener {
                //        showError(it.message ?: "Error sending reset email")
                //    }
                
                // Mock successful password reset
                Toast.makeText(this, "Reset link sent to your email.", Toast.LENGTH_SHORT).show()
            } else {
                binding.emailLayout.error = "Please enter your email"
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.emailLayout.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email format"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordLayout.error = "Password is required"
            isValid = false
        } else {
            binding.passwordLayout.error = null
        }

        return isValid
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)
        // Temporarily bypass Firebase auth
        // auth.signInWithEmailAndPassword(email, password)
        //    .addOnCompleteListener(this) { task ->
        //        showLoading(false)
        //        if (task.isSuccessful) {
        //            val user = auth.currentUser
        //            if (user != null && user.isEmailVerified) {
        //                navigateToMain()
        //            } else {
        //                auth.signOut()
        //                showError("Please verify your email before logging in.")
        //            }
        //        } else {
        //            showError(task.exception?.message ?: "Authentication failed")
        //        }
        //    }
        
        // Mock successful login for development
        showLoading(false)
        navigateToMain()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.loginButton.isEnabled = !show
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
