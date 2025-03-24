package com.nhiho.unplashwallpaper.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.nhiho.unplashwallpaper.R
import com.nhiho.unplashwallpaper.ui.PhotoAdapter
import com.nhiho.unplashwallpaper.ui.PhotoViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var viewModel: PhotoViewModel
    private var page = 1
    private var currentQuery: String = "nature" // Mặc định tìm kiếm ảnh thiên nhiên

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        searchView = findViewById(R.id.searchView)

        viewModel = ViewModelProvider(this)[PhotoViewModel::class.java]
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        swipeRefreshLayout.setOnRefreshListener {
            page++
            viewModel.loadPhotos(currentQuery, page)
        }

        // Xử lý sự kiện tìm kiếm
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    currentQuery = query
                    page = 1
                    viewModel.loadPhotos(currentQuery, page)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        viewModel.photos.observe(this) { photos ->
            swipeRefreshLayout.isRefreshing = false
            if (photos.isNotEmpty()) {
                recyclerView.adapter = PhotoAdapter(photos) { photo ->
                    val intent = Intent(this, DetailActivity::class.java).apply {
                        putExtra("PHOTO_URL", photo.urls.regular)
                    }
                    startActivity(intent)
                }
            } else {
                Toast.makeText(this, "Không có ảnh nào!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadPhotos(currentQuery, page)
    }
}
