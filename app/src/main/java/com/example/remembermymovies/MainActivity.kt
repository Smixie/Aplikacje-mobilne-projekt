package com.example.remembermymovies

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        usernameInput = findViewById(R.id.email_address_login)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        // Add underline text to TextView
        createAccount = findViewById(R.id.create_account)
        val underline = SpannableString("Nie masz jeszcze konta? Stwórz je!")
        underline.setSpan(UnderlineSpan(), 0, underline.length, 0)
        createAccount.text = underline

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            Log.i("Test Credentianls", "Username : $username and Password $password")
        }

        createAccount.setOnClickListener {
            Log.i("Test Create User Link", "Clicked")
            val intent = Intent(this, CreateAnAccountActivity::class.java)
            startActivity(intent)
        }
    }
}