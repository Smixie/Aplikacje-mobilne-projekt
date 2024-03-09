package com.example.remembermymovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    private lateinit var splash: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        splash = findViewById(R.id.splash)
        splash.postDelayed(
            {
            startActivity(Intent(this, LoginActivity::class.java))
            },2000)



    }
}