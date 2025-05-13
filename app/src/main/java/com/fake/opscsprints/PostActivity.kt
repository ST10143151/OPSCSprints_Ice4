package com.fake.loginregistration

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.*

class PostActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var captionEditText: EditText
    private lateinit var postButton: Button
    private lateinit var selectImageButton: Button

    private var capturedBitmap: Bitmap? = null
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            capturedBitmap = it
            imageView.setImageBitmap(it)
        } ?: Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        imageView = findViewById(R.id.imagePreview)
        captionEditText = findViewById(R.id.captionEditText)
        selectImageButton = findViewById(R.id.selectImageButton)
        postButton = findViewById(R.id.postButton)

        selectImageButton.setOnClickListener {
            takePictureLauncher.launch(null)
        }

        postButton.setOnClickListener {
            val caption = captionEditText.text.toString().trim()
            if (caption.isEmpty() || capturedBitmap == null) {
                Toast.makeText(this, "Please enter caption and capture image", Toast.LENGTH_SHORT).show()
            } else {
                uploadPost(caption, capturedBitmap!!)
            }
        }
    }

    private fun uploadPost(caption: String, bitmap: Bitmap) {
        val userId = auth.currentUser?.uid ?: return
        val postId = UUID.randomUUID().toString()

        val base64Image = bitmapToBase64(bitmap)

        val post = hashMapOf(
            "postId" to postId,
            "imageBase64" to base64Image,
            "caption" to caption,
            "userId" to userId,
            "timestamp" to System.currentTimeMillis(),
            "likes" to 0,
            "commentCount" to 0
        )

        firestore.collection("posts").document(postId).set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Posted successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Post failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
}
