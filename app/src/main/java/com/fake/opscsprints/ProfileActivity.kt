package com.fake.opscsprints

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        
        // Simple message until Firebase is re-enabled
        Toast.makeText(this, "Profile functionality disabled temporarily", Toast.LENGTH_LONG).show()
    }
}