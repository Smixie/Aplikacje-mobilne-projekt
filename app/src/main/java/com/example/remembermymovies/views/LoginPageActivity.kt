package com.example.remembermymovies.views

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginPageActivity : AppCompatActivity() {
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccount: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var googleLogin: ImageView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        auth = Firebase.auth

        // Standard login with password and email
        emailInput = findViewById(R.id.email_address_login)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            normalLoginWithPassword(email, password)
        }

        // Log in with Google
        googleLogin = findViewById(R.id.google_login)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleLogin.setOnClickListener {
            val intent = googleSignInClient.signInIntent
            googleSignInActivityResultLauncher.launch(intent)
        }

        // Create new account with password and email
        createAccount = findViewById(R.id.create_account)
        createAccount.setOnClickListener {
            Log.i("Test Create User Link", "Clicked")
            val intent = Intent(this, CreateAnAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun normalLoginWithPassword(email: String, password: String) {
        if (check(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.i("EmailLogin", "SignInWithEmail: Success")
                        startActivity(Intent(this, MovieActivity::class.java))
                        finish()
                    } else {
                        Log.i("EmailLogin", "SignInWithEmail: Failure")
                        Toast.makeText(baseContext, "Wrong credentials!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(baseContext, "Some fields are empty!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun check(email: String, password: String): Boolean {
        return !(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
    }

    private val googleSignInActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult : ${result.data!!.extras}")

                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = accountTask.getResult(ApiException::class.java)
                    Log.d(TAG, "onActivityResult : $account")

                    firebaseAuthWithGoogleAccount(account)
                } catch (e: ApiException) {
                    Log.w(TAG, "onActivityResult : ${e.message}")
                }
            } else {
                Log.w(TAG, "onActivityResult : ${result.data}")
            }
        }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount?) {
        val credential = GoogleAuthProvider.getCredential(account!!.idToken, null)

        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")

                    if (userId != null) {
                        database.child(userId).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (!snapshot.exists()) {
                                    // User does not exist, create a new account
                                    val userName = account.displayName
                                    val data = hashMapOf(
                                        "userName" to userName
                                    )

                                    database.child(userId).setValue(data).addOnSuccessListener {
                                        Log.i("Register", "User has been added to the database!")
                                    }.addOnFailureListener {
                                        Log.i("Register", "Error occurred while adding user to the database!")
                                    }
                                } else {
                                    // User already exists, continue logging in
                                    Log.i("Login", "User already exists in the database!")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Login", "Error occurred while checking if user exists in the database!", error.toException())
                            }
                        })
                    }

                    startActivity(Intent(this, MovieActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { err ->
                Log.d(TAG, "firebaseAuthWithGoogleAccount : ${err.message}")
                Toast.makeText(this, "${err.message}", Toast.LENGTH_SHORT).show()
            }
    }
}