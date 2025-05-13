package com.fake.loginregistration

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fake.loginregistration.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import android.util.Log

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.registerButton.setOnClickListener {
            val fullName = binding.nameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            if (validateInputs(fullName, email, password, confirmPassword)) {
                registerUser(fullName, email, password)
            }
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(fullName: String, email: String, password: String, confirmPassword: String): Boolean {
        var valid = true

        if (fullName.isEmpty()) {
            binding.nameLayout.error = "Name required"
            valid = false
        } else {
            binding.nameLayout.error = null
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.error = "Invalid email"
            valid = false
        } else {
            binding.emailLayout.error = null
        }

        if (password.length < 6) {
            binding.passwordLayout.error = "Password must be at least 6 characters"
            valid = false
        } else {
            binding.passwordLayout.error = null
        }

        if (password != confirmPassword) {
            binding.confirmPasswordLayout.error = "Passwords do not match"
            valid = false
        } else {
            binding.confirmPasswordLayout.error = null
        }

        return valid
    }

    private fun registerUser(fullName: String, email: String, password: String) {
        showLoading(true)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = task.result?.user
                    firebaseUser?.sendEmailVerification()

                    firebaseUser?.let {
                        saveUserToFirestore(it.uid, fullName, email)
                    }

                    Toast.makeText(
                        this,
                        "Registration successful! Please verify your email.",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    showLoading(false)
                    showError(task.exception?.message ?: "Registration failed.")
                }
            }
    }

    private fun saveUserToFirestore(uid: String, fullName: String, email: String) {
        val userData = hashMapOf(
            "uid" to uid,
            "name" to fullName,
            "email" to email,
            "profilePicUrl" to "",
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        firestore.collection("users").document(uid).set(userData)
            .addOnSuccessListener {
                showLoading(false)
                Log.d("Firestore", "✅ User document saved successfully!")
                Toast.makeText(this, "User saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                showLoading(false)
                Log.e("Firestore", "❌ Failed to save user document: ${e.message}", e)
                Toast.makeText(this, "Firestore error: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showLoading(show: Boolean) {
        binding.registerButton.isEnabled = !show

    }

    private fun showError(message: String) {
        Log.e("RegistrationError", message)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
