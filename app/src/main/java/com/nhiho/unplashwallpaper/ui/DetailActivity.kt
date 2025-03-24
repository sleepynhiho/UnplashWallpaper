package com.nhiho.unplashwallpaper.ui

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nhiho.unplashwallpaper.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DetailActivity : AppCompatActivity() {
    private lateinit var imageViewDetail: ImageView
    private lateinit var btnBack: Button
    private lateinit var btnSave: Button
    private lateinit var btnShare: Button
    private lateinit var btnFullScreen: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        imageViewDetail = findViewById(R.id.imageViewDetail)
        btnBack = findViewById(R.id.btnBack)
        btnSave = findViewById(R.id.btnSave)
        btnShare = findViewById(R.id.btnShare)
        btnFullScreen = findViewById(R.id.btnFullScreen)

        val imageUrl = intent.getStringExtra("PHOTO_URL")

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(imageViewDetail)
        }

        btnBack.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            if (imageUrl != null) saveImage(imageUrl)
        }

        btnShare.setOnClickListener {
            if (imageUrl != null) shareImage(imageUrl)
        }

        btnFullScreen.setOnClickListener {
            imageViewDetail.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

    private fun saveImage(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<android.graphics.Bitmap>() {
                override fun onResourceReady(resource: android.graphics.Bitmap, transition: Transition<in android.graphics.Bitmap>?) {
                    val filename = "wallpaper_${System.currentTimeMillis()}.jpg"

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Android 10+ dùng MediaStore API
                        val values = ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Wallpapers")
                        }

                        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                        uri?.let {
                            contentResolver.openOutputStream(it)?.use { outputStream ->
                                resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            }
                            Toast.makeText(this@DetailActivity, "Image saved to Gallery!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Android 9 trở xuống: Lưu vào thư mục ngoài
                        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Wallpapers")
                        if (!directory.exists()) directory.mkdirs()

                        val file = File(directory, filename)
                        try {
                            val outputStream = FileOutputStream(file)
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                            outputStream.flush()
                            outputStream.close()

                            // Thêm file vào thư viện ảnh
                            MediaScannerConnection.scanFile(this@DetailActivity, arrayOf(file.absolutePath), arrayOf("image/jpeg"), null)

                            Toast.makeText(this@DetailActivity, "Image saved to ${file.absolutePath}", Toast.LENGTH_SHORT).show()
                        } catch (e: IOException) {
                            e.printStackTrace()
                            Toast.makeText(this@DetailActivity, "Save failed!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }


    private fun shareImage(url: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Check out this wallpaper: $url")
        }
        startActivity(Intent.createChooser(intent, "Share via"))
    }
}
