package com.example.remembermymovies.views

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.databinding.ActivityDetailBinding
import com.example.remembermymovies.modele.MovieDetails
import com.example.remembermymovies.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cofanie sie
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        val movieId = intent.getStringExtra("MOVIE_ID") ?: ""
        fetchData(movieId)

        // Cofanie sie ->
        backArrow(binding.backArrow)

        // Dodanie filmu do listy do obejrzenia
        manageToWatchList(movieId)

        // Sprawdzenie czy film znajduje sie juz w bazie danych
        checkIfMovieExistsInDatabase(movieId)
    }

    fun backArrow(view: android.view.View) {
        val backArrow: ImageView = findViewById(R.id.back_arrow)
        backArrow.setOnClickListener {
            finish()
        }
    }

    fun fetchData(movieId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = RetrofitClient.webService.getMovieDetail(movieId, Constants.API_KEY)

            if (response.isSuccessful) {
                val movie: MovieDetails = response.body()!!

                val detailTitle: TextView = findViewById(R.id.detailTitle)
                val detailImage: ImageView = findViewById(R.id.detailImagePoster)
                val releaseGenresTime: TextView = findViewById(R.id.releaseGenresTime)
                val moviesDescription: TextView = findViewById(R.id.detailDescription)

                withContext(Dispatchers.Main) {
                    detailTitle.text = movie.title
                    releaseGenresTime.text =
                        movie.releaseDate + " | " + movie.genres.joinToString { it.name } + " | " +
                                movie.runtime + " min"
                    moviesDescription.text = movie.overview

                    Glide.with(this@DetailActivity)
                        .load(Constants.IMAGE_URL + movie.backdropPath)
                        .into(detailImage)
                }
            }
        }
    }

    fun manageToWatchList(movieId: String) {
        database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")

        binding.heartIcon.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val movieReference = database.child(userId).child("movies")

                movieReference.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var movieToDelete: DataSnapshot? = null

                        for (movie in dataSnapshot.children) {
                            if (movie.value == movieId) {
                                movieToDelete = movie
                                break
                            }
                        }

                        if (movieToDelete != null) {
                            Log.d("DetailActivity", "Movie already exists in the database")
                            movieToDelete.ref.removeValue().addOnSuccessListener {
                                Log.d("DetailActivity", "Movie removed from the database")
                                binding.heartIcon.setImageResource(R.drawable.ic_heart_empty)
                            }.addOnFailureListener {
                                Log.d("DetailActivity", "Error while removing movie from the database")
                            }
                        } else {
                            movieReference.push().setValue(movieId).addOnSuccessListener {
                                Log.d("DetailActivity", "Movie added to the database")
                                binding.heartIcon.setImageResource(R.drawable.ic_heart_filled)
                            }.addOnFailureListener {
                                Log.d("DetailActivity", "Error while adding movie to the database")
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w("DetailActivity", "loadMovie:onCancelled", error.toException())
                    }
                })
            } else {
                Log.d("DetailActivity", "User is not logged in")
            }
        }
    }

    fun checkIfMovieExistsInDatabase(movieId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val movieReference = database.child(userId).child("movies")

            movieReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var movieExists = false

                    for (movie in dataSnapshot.children) {
                        if (movie.value == movieId) {
                            movieExists = true
                            break
                        }
                    }

                    if (movieExists) {
                        binding.heartIcon.setImageResource(R.drawable.ic_heart_filled)
                    } else {
                        binding.heartIcon.setImageResource(R.drawable.ic_heart_empty)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("DetailActivity", "loadMovie:onCancelled", error.toException())
                }
            })
        }
    }
}