package com.nhiho.unplashwallpaper.repository

import android.util.Log
import com.nhiho.unplashwallpaper.data.api.RetrofitInstance
import com.nhiho.unplashwallpaper.model.UnsplashPhoto

class UnsplashRepository {
    private val api = RetrofitInstance.api
    private val clientId = "I4Rk3hmIiUhyBS33Wge8UGA9jnkejLqbzI9E_DsvNmA"

    suspend fun getPhotos(page: Int): List<UnsplashPhoto>? {
        return try {
            val response = api.getPhotos(clientId, page)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("API_ERROR", "Error fetching photos: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred", e)
            null
        }
    }

    suspend fun searchPhotos(query: String, page: Int): List<UnsplashPhoto>? {
        return try {
            val response = api.searchPhotos(clientId, query, page)
            if (response.isSuccessful) {
                response.body()?.results
            } else {
                Log.e("API_ERROR", "Error searching photos: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Exception occurred", e)
            null
        }
    }
}
