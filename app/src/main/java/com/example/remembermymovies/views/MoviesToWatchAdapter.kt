package com.example.remembermymovies.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.modele.MovieDetails

class ToWatchAdapter(private var movies: List<MovieDetails>) : RecyclerView.Adapter<ToWatchAdapter.ToWatchViewHolder>() {

    class ToWatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTextToWatch = itemView.findViewById<TextView>(R.id.tvTitleToWatch)
        val ivPosterToWatch = itemView.findViewById<ImageView>(R.id.ivPosterToWatch)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToWatchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.to_watch_item, parent, false)
        return ToWatchViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToWatchViewHolder, position: Int) {
        val movies = movies[position]
        holder.tvTextToWatch.text = movies.title

        Glide.with(holder.itemView)
            .load("${Constants.IMAGE_URL}${movies.posterPath}")
            .apply(RequestOptions().override(Constants.image_width/4, Constants.image_height/4))
            .into(holder.ivPosterToWatch)
    }

    override fun getItemCount() = movies.size

    fun updateMovies(newMovies: List<MovieDetails>) {
        this.movies = newMovies
        notifyDataSetChanged()
    }
}