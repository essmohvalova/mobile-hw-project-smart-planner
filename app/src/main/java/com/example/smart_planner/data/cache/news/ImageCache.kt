package com.example.smart_planner.data.cache.news

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCache @Inject constructor(
    private val context: Context
) {
    private val cacheDir = File(context.cacheDir, "news_images")
    private val maxCacheSize = 50 * 1024 * 1024L // 50 MB
    private val connectTimeout = 10000 // 10 seconds
    private val readTimeout = 10000 // 10 seconds

    init {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    suspend fun getImage(url: String): Bitmap? = withContext(Dispatchers.IO) {
        val fileName = generateFileName(url)
        val file = File(cacheDir, fileName)

        // Проверяем кэш
        if (file.exists()) {
            try {
                file.setLastModified(System.currentTimeMillis())
                return@withContext BitmapFactory.decodeFile(file.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                file.delete()
            }
        }

        // Загружаем изображение
        return@withContext downloadAndCacheImage(url, fileName)
    }

    private fun downloadAndCacheImage(urlString: String, fileName: String): Bitmap? {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = this@ImageCache.connectTimeout
                readTimeout = this@ImageCache.readTimeout
                doInput = true
                requestMethod = "GET"
            }

            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)

                if (bitmap != null) {
                    saveImage(bitmap, fileName)
                    return bitmap
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        return null
    }

    private fun saveImage(bitmap: Bitmap, fileName: String) {
        try {
            val file = File(cacheDir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }
            cleanCacheIfNeeded()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cleanCacheIfNeeded() {
        val files = cacheDir.listFiles() ?: return
        var totalSize = files.sumOf { it.length() }

        if (totalSize > maxCacheSize) {
            val sortedFiles = files.sortedBy { it.lastModified() }
            val targetSize = (maxCacheSize * 0.7).toLong()

            for (file in sortedFiles) {
                if (totalSize <= targetSize) break
                val fileSize = file.length()
                if (file.delete()) {
                    totalSize -= fileSize
                }
            }
        }
    }

    private fun generateFileName(url: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(url.toByteArray())
            hash.joinToString("") { "%02x".format(it) } + ".jpg"
        } catch (e: Exception) {
            // Fallback: используем часть URL
            url.hashCode().toString() + ".jpg"
        }
    }
}