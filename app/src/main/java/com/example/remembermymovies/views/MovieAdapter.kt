package com.example.remembermymovies.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.remembermymovies.R
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.modele.Movie


class MovieAdapter(
    private val context: Context,
    var listMovies: List<Movie>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivPoster = itemView.findViewById<ImageView>(R.id.ivPoster) as ImageView
        val progressIndicator =
            itemView.findViewById<CircularProgressIndicator>(R.id.progressIndicator) as CircularProgressIndicator

    }

    interface OnItemClickListener {
        fun onItemClick(movieId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
        val movie = listMovies[position]

        holder.itemView.setOnClickListener {
            listener.onItemClick(movie.id)
        }

        Glide
            .with(context)
            .load("${Constants.IMAGE_URL}${movie.posterPath}")
            .apply(RequestOptions().override(Constants.image_width, Constants.image_height))
            .into(holder.ivPoster)

        holder.progressIndicator.maxProgress = Constants.max_rating
        holder.progressIndicator.setCurrentProgress(movie.rating.toDouble())
    }

    override fun getItemCount(): Int {
        return listMovies.size
    }
}