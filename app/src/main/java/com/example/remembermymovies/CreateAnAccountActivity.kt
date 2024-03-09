package com.example.remembermymovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.remembermymovies.databinding.ActivityCreateAnAccountBinding
import com.google.firebase.auth.FirebaseAuth


class CreateAnAccountActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreateAnAccountBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityCreateAnAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButton = findViewById(R.id.back_to_login)
        backButton.setOnClickListener {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }

        binding.registerButton.setOnClickListener {
            val registerEmail = binding.emailAddressRegister.text.toString()
            val password1 = binding.passwordInputRegister.text.toString()
            val password2 = binding.password2InputRegister.text.toString()

            if (TextUtils.isEmpty(registerEmail) || TextUtils.isEmpty(password1) ||
                TextUtils.isEmpty(password2)
            ) {
                Toast.makeText(baseContext, "Some fields are empty!", Toast.LENGTH_SHORT).show()
                Log.i("Register", "Some fields are empty!")
            } else if (password1 != password2) {
                Toast.makeText(baseContext, "Passwords does not match!", Toast.LENGTH_SHORT).show()
                Log.i("Register", "Passwords does not match!")
            } else if (password1.length < 6) {
                Toast.makeText(baseContext, "Password is too short!", Toast.LENGTH_SHORT).show()
                Log.i("Register", "Password is too short!")
            } else {
                auth.createUserWithEmailAndPassword(registerEmail, password1)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(baseContext, LoginPageActivity::class.java)
                            startActivity(intent)
                            Log.i("Register", "Account has been created successfully!")
                        } else {
                            Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                                .show()
                            Log.i("Register", "Error occurred! " + task.exception.toString())
                        }
                    }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
        }
    }
}