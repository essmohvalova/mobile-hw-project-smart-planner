package com.example.smart_planner.data.api.news

import com.example.smart_planner.data.models.news.NewsResponse
import com.example.smart_planner.data.models.news.TopStoriesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NewsApiService {
    @GET("topstories/v2/{section}.json")
    suspend fun getNews(
        @Path("section") section: String,
        @Query("api-key") apiKey: String
    ): TopStoriesResponse
}