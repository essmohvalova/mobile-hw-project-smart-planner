package com.example.smart_planner.presentation.utils

import android.graphics.Bitmap
import android.widget.ImageView
import com.example.smart_planner.data.cache.news.ImageCache
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageLoader @Inject constructor(
    private val imageCache: ImageCache
) {

    fun loadImage(url: String?, imageView: ImageView, placeholderResId: Int = 0) {
        if (url.isNullOrBlank()) {
            setPlaceholder(imageView, placeholderResId)
            return
        }

        // Показываем плейсхолдер во время загрузки
        if (placeholderResId != 0) {
            imageView.setImageResource(placeholderResId)
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    imageCache.getImage(url)
                }

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap)
                    Timber.d("Image loaded successfully: ${url.take(50)}...")
                } else {
                    setPlaceholder(imageView, placeholderResId)
                    Timber.w("Failed to load image: $url")
                }
            } catch (e: Exception) {
                setPlaceholder(imageView, placeholderResId)
                Timber.e(e, "Error loading image: $url")
            }
        }
    }

    private fun setPlaceholder(imageView: ImageView, placeholderResId: Int) {
        if (placeholderResId != 0) {
            imageView.setImageResource(placeholderResId)
        } else {
            imageView.setImageDrawable(null)
        }
    }
}