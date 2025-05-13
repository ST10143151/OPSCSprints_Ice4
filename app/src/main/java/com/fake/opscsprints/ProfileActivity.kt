package com.fake.loginregistration

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.fake.loginregistration.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    // Use the new Activity Result API to take a picture (returns a Bitmap preview)
    private val takePicturePreviewLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            // Display the captured photo in the ImageView
            binding.ivProfilePicture.setImageBitmap(bitmap)
            // TODO: Optionally upload to Firebase Storage and update user's photoURL
        } else {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout via view binding
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Get the current user (if logged in)
        val user = auth.currentUser

        // Show user info
        binding.tvDisplayName.text = user?.displayName ?: "No Name"
        binding.tvEmail.text = user?.email ?: "No Email"

        // Clicking the profile picture triggers camera preview
        binding.ivProfilePicture.setOnClickListener {
            takePicturePreviewLauncher.launch(null)
        }
    }
}
