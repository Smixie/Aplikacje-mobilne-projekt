package com.example.remembermymovies.views

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.modele.MovieDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MovieToWatchAdapter(private var movies: List<MovieDetails>) :
    RecyclerView.Adapter<MovieToWatchAdapter.ToWatchViewHolder>() {
    private lateinit var database: DatabaseReference

    class ToWatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTextToWatch = itemView.findViewById<TextView>(R.id.tvTitleToWatch) as TextView
        val ivPosterToWatch = itemView.findViewById<ImageView>(R.id.ivPosterToWatch) as
                ImageView
        val button = itemView.findViewById<Button>(R.id.btnMarkAsWatched) as Button
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToWatchViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.to_watch_item, parent, false)
        return ToWatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToWatchViewHolder, position: Int) {
        val movies = movies[position]
        holder.tvTextToWatch.text = movies.title

        Glide.with(holder.itemView)
            .load("${Constants.IMAGE_URL}${movies.posterPath}")
            .apply(RequestOptions().override(Constants.image_width / 4, Constants.image_height / 4))
            .into(holder.ivPosterToWatch)

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        database = FirebaseDatabase.getInstance(Constants.DATABASE_URL).reference.child("users")
            .child(userId!!).child("movies")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (movieSnapshot in snapshot.children) {
                    val id = movieSnapshot.child("id").getValue(String::class.java)
                    if (id == movies.id) {
                        val watched = movieSnapshot.child("watched").getValue(Boolean::class.java)
                        if (watched == true) {
                            applySepiaFilter(holder.ivPosterToWatch)
                        }
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MovieToWatchAdapter", "Error fetching movie watched status", error.toException())
            }
        })


        holder.button.setOnClickListener {
            applySepiaFilter(holder.ivPosterToWatch)
            markAsWatched(movies.id, database)
        }
    }

    private fun markAsWatched(movieId: String, database: DatabaseReference) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (movieSnapshot in snapshot.children) {
                    val id = movieSnapshot.child("id").getValue(String::class.java)
                    if (id == movieId) {
                        movieSnapshot.ref.child("watched").setValue(true)
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MovieToWatchAdapter", "Error updating movie watched status", error.toException())
            }
        })
    }

    private fun applySepiaFilter(imageView: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(matrix)
        imageView.colorFilter = filter
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<MovieDetails>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}