package com.example.smart_planner.data.models.news

import com.example.smart_planner.domain.models.news.News
import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class NewsResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("copyright")
    val copyright: String,
    @SerializedName("num_results")
    val numResults: Int,
    @SerializedName("results")
    val results: List<NewsDto>
)

data class NewsDto(
    @SerializedName("_id")
    val id: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("abstract")
    val abstract: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("published_date")
    val publishedDate: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("media")
    val media: List<MediaDto>?
) {
    fun toDomain(): News {
        return News(
            id = id,
            title = title,
            abstract = abstract,
            source = source,
            publishedDate = parseDate(publishedDate),
            imageUrl = extractImageUrl(),
            url = url
        )
}
    private fun parseDate(dateString: String): Date {
        return try {
            // Пробуем разные форматы даты
            val formats = listOf(
                "yyyy-MM-dd'T'HH:mm:ssZ",
                "yyyy-MM-dd'T'HH:mm:ssXXX",
                "yyyy-MM-dd HH:mm:ss"
            )

            for (format in formats) {
                try {
                    return SimpleDateFormat(format, Locale.US).parse(dateString) ?: Date()
                } catch (e: Exception) {
                    // Пробуем следующий формат
                }
            }
            Date()
        } catch (e: Exception) {
            Date()
        }
    }

    private fun extractImageUrl(): String? {
        return media
            ?.firstOrNull { it.type == "image" }
            ?.metadata
            ?.lastOrNull()
            ?.url
    }
}

data class MediaDto(
    @SerializedName("type")
    val type: String,
    @SerializedName("media-metadata")
    val metadata: List<MediaMetadataDto>?
)

data class MediaMetadataDto(
    @SerializedName("url")
    val url: String,
    @SerializedName("format")
    val format: String
)