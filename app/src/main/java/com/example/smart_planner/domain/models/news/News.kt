package com.example.smart_planner.domain.models.news

import java.util.Date

/**
 * Доменная модель новости - используется во всем приложении
 * Не содержит зависимостей от фреймворков и библиотек
 */
data class News(
    val id: String,
    val title: String,
    val abstract: String,
    val source: String,
    val publishedDate: Date,
    val imageUrl: String?,
    val url: String
) {
    // Бизнес-логика, относящаяся к новости
    fun isRecent(): Boolean {
        val oneDayAgo = Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000)
        return publishedDate.after(oneDayAgo)
    }

    fun hasImage(): Boolean {
        return !imageUrl.isNullOrBlank()
    }

    fun getDisplaySource(): String {
        return when {
            source.contains("New York Times") -> "NYT"
            source.contains("Associated Press") -> "AP"
            else -> source
        }
    }
}