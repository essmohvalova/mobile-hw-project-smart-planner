package com.example.smart_planner.data.repository.news

import com.example.smart_planner.domain.models.news.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNewsStream(): Flow<List<News>>
    suspend fun refreshNews()
    suspend fun clearCache()
}