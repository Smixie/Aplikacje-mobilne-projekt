package com.example.remembermymovies.network.response

import com.example.remembermymovies.modele.Movie
import com.google.gson.annotations.SerializedName

data class MovieResponse(
    @SerializedName("results")
    val results: List<Movie>
)
