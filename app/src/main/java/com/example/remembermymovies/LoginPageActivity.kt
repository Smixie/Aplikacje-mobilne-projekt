package com.example.remembermymovies

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class LoginPageActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccount: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        auth = Firebase.auth

        emailInput = findViewById(R.id.email_address_login)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)

        // Add underline text to TextView
        createAccount = findViewById(R.id.create_account)
        val underline = SpannableString("Nie masz jeszcze konta? Stwórz je!")
        underline.setSpan(UnderlineSpan(), 0, underline.length, 0)
        createAccount.text = underline

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this){
                task ->
                if (task.isSuccessful){
                    Log.i("EmailLogin","SignInWithEmail: Success")
                    val user = auth.currentUser
                    startActivity(Intent(this, WelcomeDashboard::class.java))
                }else{
                    Log.i("EmailLogin", "SignInWithEmail: Failure")
                    Toast.makeText(baseContext,"Wrong credentials!",Toast.LENGTH_SHORT).show()
                }
            }

            Log.i("Test Credentianls", "Username : $email and Password $password")
        }

        createAccount.setOnClickListener {
            Log.i("Test Create User Link", "Clicked")
            startActivity(Intent(this, CreateAnAccountActivity::class.java))
        }
    }
}