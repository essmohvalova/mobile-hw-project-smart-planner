package com.example.smart_planner.data.models.news

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.smart_planner.domain.models.news.News
import java.util.Date

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val abstract: String,
    val source: String,
    val publishedDate: Long,
    val imageUrl: String?,
    val url: String,
    val cacheTimestamp: Long = System.currentTimeMillis()
) {
    // Маппер из Entity в Domain модель
    fun toDomain(): News {
        return News(
            id = id,
            title = title,
            abstract = abstract,
            source = source,
            publishedDate = Date(publishedDate),
            imageUrl = imageUrl,
            url = url
        )
    }

    companion object {
        // Маппер из Domain модели в Entity
        fun fromDomain(news: News): NewsEntity {
            return NewsEntity(
                id = news.id,
                title = news.title,
                abstract = news.abstract,
                source = news.source,
                publishedDate = news.publishedDate.time,
                imageUrl = news.imageUrl,
                url = news.url,
                cacheTimestamp = System.currentTimeMillis()
            )
        }
    }
}