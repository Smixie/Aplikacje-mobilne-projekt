package com.example.remembermymovies.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.databinding.ActivityCreateAnAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class CreateAnAccountActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCreateAnAccountBinding
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        binding = ActivityCreateAnAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        backButton = findViewById(R.id.back_to_login)
        backButton.setOnClickListener {
            finish()
        }

        binding.registerButton.setOnClickListener {
            signUpUser()
            finish()
        }
    }

    private fun signUpUser() {
        val registerEmail = binding.emailAddressRegister.text.toString()
        val password1 = binding.passwordInputRegister.text.toString()
        val password2 = binding.password2InputRegister.text.toString()
        val userName = binding.userName.text.toString()


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
                        val userId = auth.currentUser?.uid
                        if (userId != null) {
                            database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")

                            database.child(userId).child("movies").setValue("").addOnSuccessListener {
                                Log.i("Register", "User has been added to the database!")
                            }.addOnFailureListener {
                                Log.i("Register", "Error occurred while adding user to the database!")
                            }
                            database.child(userId).child("userName").setValue(userName).addOnSuccessListener {
                                Log.i("Register", "User name has been added to the database!")
                            }.addOnFailureListener {
                                Log.i("Register", "Error occurred while adding user name to the database!")
                            }
                        }

                        finish()
                        Log.i("Register", "Account has been created successfully!")
                    } else {
                        Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                            .show()
                        Log.i("Register", "Error occurred! " + task.exception.toString())
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()

        if (auth.currentUser != null) {
            val intent = Intent(this, LoginPageActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}