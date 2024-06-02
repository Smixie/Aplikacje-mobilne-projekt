package com.example.remembermymovies.modele

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: String,

    @SerializedName("original_title")
    val originalTitle: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("vote_average")
    val rating: Double,

    @SerializedName("vote_count")
    val voteCount: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("backdrop_path")
    val backdropPath: String
)
