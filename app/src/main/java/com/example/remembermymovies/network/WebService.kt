package com.example.remembermymovies.network

import com.example.remembermymovies.modele.MovieDetails
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import com.example.remembermymovies.network.response.MovieResponse
import retrofit2.http.Path

interface WebService {
    @GET("now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String,
    ): Response<MovieResponse>

    @GET("popular")
    suspend fun getPopular(
        @Query("api_key") apiKey: String, @Query("page") page: Int
    ): Response<MovieResponse>

    @GET("upcoming")
    suspend fun getUpcoming(
        @Query("api_key") apiKey: String,
    ): Response<MovieResponse>

    @GET("{movie_id}")
    suspend fun getMovieDetail(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String,
    ): Response<MovieDetails>
}