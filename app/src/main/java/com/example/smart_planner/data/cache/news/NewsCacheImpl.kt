package com.example.smart_planner.data.cache.news

import com.example.smart_planner.data.database.news.NewsDao
import com.example.smart_planner.data.database.news.NewsEntity
import com.example.smart_planner.domain.models.news.News
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsCacheImpl @Inject constructor(
    private val newsDao: NewsDao
) : NewsCache {

    companion object {
        private const val CACHE_DURATION = 30 * 60 * 1000L // 30 минут
    }

    override fun getNews(): Flow<List<News>> {
        return newsDao.getAllNews().map { entities ->
            entities.map { entity ->
                News(
                    id = entity.id,
                    title = entity.title,
                    abstract = entity.abstractText,
                    source = entity.source,
                    publishedDate = Date(entity.publishedDate),
                    imageUrl = entity.imageUrl,
                    url = entity.url
                )
            }
        }
    }

    override suspend fun saveNews(news: List<News>) {
        withContext(Dispatchers.IO) {
            // Очищаем устаревшие записи
            clearExpired()

            // Конвертируем Domain модели в Entity
            val entities = news.map { domainNews ->
                NewsEntity(
                    id = domainNews.id,
                    title = domainNews.title,
                    abstractText = domainNews.abstract,
                    source = domainNews.source,
                    publishedDate = domainNews.publishedDate.time,
                    imageUrl = domainNews.imageUrl,
                    url = domainNews.url,
                    cacheTimestamp = System.currentTimeMillis()
                )
            }

            newsDao.insertAll(entities)
        }
    }

    override suspend fun clearExpired() {
        withContext(Dispatchers.IO) {
            val expiryTime = System.currentTimeMillis() - CACHE_DURATION
            newsDao.deleteExpired(expiryTime)
        }
    }

    override suspend fun clearAll() {
        withContext(Dispatchers.IO) {
            newsDao.clearAll()
        }
    }
}