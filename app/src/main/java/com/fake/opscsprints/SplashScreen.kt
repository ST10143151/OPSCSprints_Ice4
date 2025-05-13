package com.fake.opscsprints

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen) // Use the splash layout

        lifecycleScope.launch {
            delay(2000) // Delay for 2 seconds, respects lifecycle
            if (!isFinishing) {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish() // Prevent going back to SplashScreen
            }
        }
    }
}
