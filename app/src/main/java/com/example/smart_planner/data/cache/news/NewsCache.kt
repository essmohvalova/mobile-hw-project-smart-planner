package com.example.smart_planner.data.cache.news

import com.example.smart_planner.domain.models.news.News
import kotlinx.coroutines.flow.Flow

interface NewsCache {
    fun getNews(): Flow<List<News>>
    suspend fun saveNews(news: List<News>)
    suspend fun clearExpired()
    suspend fun clearAll()
}