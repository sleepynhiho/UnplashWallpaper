package com.nhiho.unplashwallpaper.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhiho.unplashwallpaper.model.UnsplashPhoto
import com.nhiho.unplashwallpaper.repository.UnsplashRepository
import kotlinx.coroutines.launch

class PhotoViewModel : ViewModel() {
    private val repository = UnsplashRepository()

    private val _photos = MutableLiveData<List<UnsplashPhoto>>()
    val photos: LiveData<List<UnsplashPhoto>> get() = _photos

    private var currentQuery: String = "nature" // Từ khóa mặc định
    private var currentPage: Int = 1
    private var isLoading = false

    // Load ảnh ban đầu hoặc khi tìm kiếm
    fun loadPhotos(query: String, page: Int) {
        if (isLoading) return
        isLoading = true

        viewModelScope.launch {
            val newPhotos = if (query == currentQuery) {
                repository.getPhotos(page)
            } else {
                repository.searchPhotos(query, page)
            }

            newPhotos?.let {
                if (page == 1) {
                    _photos.postValue(it) // Nếu là trang đầu tiên, thay thế danh sách ảnh
                } else {
                    val updatedPhotos = _photos.value.orEmpty() + it
                    _photos.postValue(updatedPhotos) // Nếu không, thêm ảnh vào danh sách hiện tại
                }
            }

            currentQuery = query
            currentPage = page
            isLoading = false
        }
    }

    // Hàm tải thêm ảnh khi người dùng cuộn xuống hoặc làm mới
    fun loadMorePhotos() {
        loadPhotos(currentQuery, currentPage + 1)
    }
}
