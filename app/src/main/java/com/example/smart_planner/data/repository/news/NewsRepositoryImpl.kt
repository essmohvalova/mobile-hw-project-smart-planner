package com.example.smart_planner.data.repository.news

import com.example.smart_planner.data.api.news.AnotherApiService
import com.example.smart_planner.data.api.news.NewsApiService
import com.example.smart_planner.data.api.news.TestRequest
import com.example.smart_planner.data.cache.news.NewsCache
import com.example.smart_planner.data.models.news.TopStoryArticle  // Важно!
import com.example.smart_planner.domain.models.news.News
import com.example.smart_planner.di.NewsMapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val anotherApiService: AnotherApiService,
    private val newsCache: NewsCache,
    private val mapper: NewsMapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NewsRepository {

    companion object {
        private const val NYT_API_KEY = "YJa6px3D47E3rTeWMgfYARGPpdAqgN6ERO4C1aySda7AgzUq"
    }

    override fun getNewsStream(): Flow<List<News>> {
        return newsCache.getNews()
    }

    override suspend fun refreshNews() {
        withContext(ioDispatcher) {
            try {
                Timber.d("Refreshing news from API")
                Timber.d("Making API request with key: ${NYT_API_KEY.take(5)}...")


                // Вызываем с одним параметром section
                val response = newsApiService.getNews(
                    section = "home",  // Только section, source больше не передаем!
                    apiKey = NYT_API_KEY
                )

                Timber.d("API Response - Status: ${response.status}, Num results: ${response.numResults}")

                if (response.status == "OK") {
                    val articles = response.results
                    Timber.d("Received ${articles.size} news items")

                    if (articles.isNotEmpty()) {
                        val firstArticle = articles.first()
                        Timber.d("First article - Title: ${firstArticle.title}")
                        Timber.d("First article - Image URL: ${firstArticle.multimedia?.firstOrNull()?.url}")
                        Timber.d("First article - All multimedia formats: ${firstArticle.multimedia?.map { it.format }}")
                    }

                    val domainNews = articles.map { it.toDomain() }
                    newsCache.saveNews(domainNews)
                    Timber.d("Saved ${domainNews.size} news items to cache")
                } else {
                    Timber.e("API returned non-OK status: ${response.status}")
                }

                performAdditionalRequest()

            } catch (e: Exception) {
                Timber.e(e, "Error refreshing news")
            }
        }
    }

    private suspend fun performAdditionalRequest() {
        try {
            val request = TestRequest(
                testField = "test_value",
                value = 42
            )

            val response = anotherApiService.sendTestData(request)
            Timber.d("Additional request response: $response")
        } catch (e: Exception) {
            Timber.e(e, "Error in additional request")
        }
    }

    override suspend fun clearCache() {
        withContext(ioDispatcher) {
            newsCache.clearAll()
            Timber.d("Cache cleared")
        }
    }
}