package com.example.smart_planner.data.models.news

import com.example.smart_planner.domain.models.news.News
import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TopStoriesResponse(
    @SerializedName("status") val status: String,
    @SerializedName("copyright") val copyright: String,
    @SerializedName("section") val section: String,
    @SerializedName("last_updated") val lastUpdated: String,
    @SerializedName("num_results") val numResults: Int,
    @SerializedName("results") val results: List<TopStoryArticle>
)

data class TopStoryArticle(
    @SerializedName("title") val title: String,
    @SerializedName("abstract") val abstract: String,
    @SerializedName("url") val url: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("byline") val byline: String,
    @SerializedName("published_date") val publishedDate: String,
    @SerializedName("multimedia") val multimedia: List<TopStoryMultimedia>?,
    @SerializedName("section") val section: String,
    @SerializedName("subsection") val subsection: String
) {
    fun toDomain(): News {
        val id = uri.substringAfterLast("/").takeIf { it.isNotEmpty() } ?: uri

        // Улучшенное извлечение URL изображения
        val imageUrl = extractImageUrl()

        Timber.d("Extracted image URL for '${title.take(20)}...': $imageUrl")

        return News(
            id = id,
            title = title,
            abstract = abstract,
            source = byline.ifEmpty { "The New York Times" },
            publishedDate = parseDate(publishedDate),
            imageUrl = imageUrl,
            url = url
        )
    }

    private fun extractImageUrl(): String? {
        if (multimedia.isNullOrEmpty()) {
            return null
        }

        // Приоритет форматов изображений (от лучшего к худшему)
        val preferredFormats = listOf(
            "superJumbo",
            "mediumThreeByTwo440",
            "mediumThreeByBy225",
            "thumbLarge",
            "thumbnail"
        )

        // Сначала ищем по приоритетным форматам
        for (format in preferredFormats) {
            val multimediaItem = multimedia.find { it.format == format }
            if (multimediaItem != null) {
                val url = multimediaItem.url
                // Добавляем базовый URL если нужно
                return if (url.startsWith("http")) url else "https://static01.nyt.com/$url"
            }
        }
        val firstItem = multimedia.firstOrNull()
        return firstItem?.url?.let { url ->
            if (url.startsWith("http")) url else "https://static01.nyt.com/$url"
        }
    }

    private fun parseDate(dateString: String): Date {
        return try {
            // Формат даты из Top Stories API: 2026-03-17T16:23:15-04:00
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            format.parse(dateString) ?: Date()
        } catch (e: Exception) {
            try {
                // Альтернативный формат
                val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
                format.parse(dateString) ?: Date()
            } catch (e: Exception) {
                Date()
            }
        }
    }
}

data class TopStoryMultimedia(
    @SerializedName("url") val url: String,
    @SerializedName("format") val format: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int,
    @SerializedName("type") val type: String,
    @SerializedName("caption") val caption: String
)