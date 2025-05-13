package com.fake.opscsprints

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PostActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        
        // Simple message until Firebase is re-enabled
        Toast.makeText(this, "Posting functionality disabled temporarily", Toast.LENGTH_LONG).show()
        
        // Set up back button
        findViewById<Button>(R.id.postButton).setOnClickListener {
            finish()
        }
    }
}