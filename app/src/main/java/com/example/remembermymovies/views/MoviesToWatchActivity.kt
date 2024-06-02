package com.example.remembermymovies.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.databinding.ActivityMoviesToWatchBinding
import com.example.remembermymovies.modele.MovieDetails
import com.example.remembermymovies.network.RetrofitClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MoviesToWatchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMoviesToWatchBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: ToWatchAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesToWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        fetchMovies()

        setupDrawerNavigation()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewToWatch.layoutManager = layoutManager
        adapter = ToWatchAdapter(arrayListOf())
        binding.recyclerViewToWatch.adapter = adapter
    }

    private fun fetchMovies() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")
            .child(userId!!).child("movies")

        val movieList = ArrayList<String>()
        val moviesData = ArrayList<MovieDetails>()
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    val movieId = ds.getValue(String::class.java)
                    if (movieId != null) {
                        movieList.add(movieId)
                    }
                }

                CoroutineScope(Dispatchers.IO).launch {
                    for (id in movieList) {
                        val response = RetrofitClient.webService.getMovieDetail(id, Constants.API_KEY)
                        if (response.isSuccessful) {
                            val movie = response.body()!!
                            moviesData.add(movie)
                        }
                    }
                    delay(3000)
                    withContext(Dispatchers.Main) {
                        adapter.updateMovies(moviesData)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("MoviesToWatchActivity", "Error fetching movies", error.toException())
            }
        }
        database.addValueEventListener(valueEventListener)
    }

    private fun setupDrawerNavigation() {
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.nav_view)

        // Get user and put it into navigation drawer
        val headerView = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.nav_header_textView)

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")
        if (userId != null) {
            val userReference = database.child(userId).child("userName")
            userReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(String::class.java)
                    navUsername.text = "Cześć,\n${user}"
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MovieActivity", "Error while getting user data", error.toException())
                }
            })
        }

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_first_item -> navigateToActivity(MovieActivity::class.java)
                R.id.nav_second_item -> navigateToActivity(MoviesToWatchActivity::class.java)
                R.id.nav_logout -> {
                    FirebaseAuth.getInstance().signOut()

                    val intent = Intent(this, LoginPageActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun navigateToActivity(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
    }
}