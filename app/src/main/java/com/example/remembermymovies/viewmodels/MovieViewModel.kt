package com.example.remembermymovies.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remembermymovies.core.Constants
import com.example.remembermymovies.modele.Movie
import com.example.remembermymovies.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieViewModel: ViewModel() {
    private val repository = MutableLiveData<List<Movie>>()
    val moviesList: LiveData<List<Movie>> = repository

    fun getNowPlaying(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getNowPlaying(Constants.API_KEY)
            withContext(Dispatchers.Main){
                repository.value = response.body()!!.results.sortedByDescending {
                    it.rating
                }
            }
        }
    }

    fun getPopular(page: Int = 1){
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getPopular(Constants.API_KEY, page)
            withContext(Dispatchers.Main){
                repository.value = response.body()!!.results.sortedByDescending {
                    it.rating
                }
            }
        }
    }

    fun getUpcoming(){
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.webService.getUpcoming(Constants.API_KEY)
            withContext(Dispatchers.Main){
                repository.value = response.body()!!.results.sortedByDescending {
                    it.rating
                }
            }
        }
    }
}