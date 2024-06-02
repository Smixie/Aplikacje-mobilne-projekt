package com.example.remembermymovies.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.remembermymovies.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var splash: ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        splash = findViewById(R.id.splash)
        splash.postDelayed(
            {
                updateUI(currentUser)
            }, 2000
        )
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            // User is signed in, start MovieActivity
            val intent = Intent(this, MovieActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // User is signed out, start LoginPageActivity
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}