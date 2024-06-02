package com.example.remembermymovies.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.databinding.ActivityMovieBinding
import com.example.remembermymovies.viewmodels.MovieViewModel
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MovieActivity : AppCompatActivity(), MovieAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMovieBinding
    private lateinit var viewModel: MovieViewModel
    private lateinit var adapter: MovieAdapter
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawerNavigation()

        viewModel = ViewModelProvider(this)[MovieViewModel::class.java]

        setupRecyclerView()

        viewModel.moviesList.observe(this) {
            adapter.listMovies = it
            adapter.notifyDataSetChanged()
        }

        viewModel.getPopular(1)
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
                R.id.nav_second_item -> navigateToActivity(MovieToWatchActivity::class.java)
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

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.layoutManager = layoutManager
        adapter = MovieAdapter(this, arrayListOf(), this)
        binding.recyclerView.adapter = adapter
    }

    override fun onItemClick(movieId: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("MOVIE_ID", movieId)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}