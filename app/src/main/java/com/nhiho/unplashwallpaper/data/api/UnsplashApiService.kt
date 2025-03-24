package com.nhiho.unplashwallpaper.data.api

import com.nhiho.unplashwallpaper.model.UnsplashPhoto
import com.nhiho.unplashwallpaper.model.UnsplashResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApiService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("client_id") clientId: String,
        @Query("page") page: Int
    ): Response<List<UnsplashPhoto>>

    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("client_id") clientId: String,
        @Query("query") query: String,
        @Query("page") page: Int
    ): Response<UnsplashResponse>
}
