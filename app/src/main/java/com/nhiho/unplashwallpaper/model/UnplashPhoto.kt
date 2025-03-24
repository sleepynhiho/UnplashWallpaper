package com.nhiho.unplashwallpaper.model

import com.google.gson.annotations.SerializedName

data class UnsplashPhoto(
    val id: String,
    @SerializedName("urls") val urls: PhotoUrls
)

data class PhotoUrls(
    @SerializedName("regular") val regular: String
)
