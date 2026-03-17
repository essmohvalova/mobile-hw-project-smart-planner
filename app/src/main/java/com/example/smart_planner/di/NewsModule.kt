package com.example.smart_planner.di

import com.example.smart_planner.data.cache.news.ImageCache
import com.example.smart_planner.data.cache.news.NewsCache
import com.example.smart_planner.data.cache.news.NewsCacheImpl
import com.example.smart_planner.data.database.news.NewsDao
import com.example.smart_planner.data.database.news.NewsDatabase
import com.example.smart_planner.data.models.news.NewsDto
import com.example.smart_planner.data.models.news.NewsEntity
import com.example.smart_planner.data.repository.news.NewsRepository
import com.example.smart_planner.data.repository.news.NewsRepositoryImpl
import com.example.smart_planner.domain.models.news.News
import com.example.smart_planner.presentation.utils.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NewsModule {

    @Provides
    @Singleton
    fun provideNewsDatabase(application: android.app.Application): NewsDatabase {
        return NewsDatabase.getInstance(application)
    }

    @Provides
    @Singleton
    fun provideNewsDao(database: NewsDatabase): NewsDao {
        return database.newsDao()
    }

    @Provides
    @Singleton
    fun provideNewsCache(dao: NewsDao): NewsCache {
        return NewsCacheImpl(dao)
    }

    @Provides
    @Singleton
    fun provideImageCache(application: android.app.Application): ImageCache {
        return ImageCache(application)
    }

    @Provides
    @Singleton
    fun provideImageLoader(imageCache: ImageCache): ImageLoader {
        return ImageLoader(imageCache)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(
        newsApiService: com.example.smart_planner.data.api.news.NewsApiService,
        anotherApiService: com.example.smart_planner.data.api.news.AnotherApiService,
        newsCache: NewsCache,
        mapper: NewsMapper
    ): NewsRepository {
        return NewsRepositoryImpl(newsApiService, anotherApiService, newsCache, mapper)
    }

    @Provides
    @Singleton
    fun provideNewsMapper(): NewsMapper {
        return NewsMapper()
    }
}

// Маппер для новостей
class NewsMapper {

    fun mapDtoToDomain(dto: NewsDto): News {
        return dto.toDomain()
    }

    fun mapEntityToDomain(entity: NewsEntity): News {
        return entity.toDomain()
    }

    fun mapDomainToEntity(domain: News): NewsEntity {
        return NewsEntity.fromDomain(domain)
    }

    fun mapDtoListToDomain(dtos: List<NewsDto>): List<News> {
        return dtos.map { it.toDomain() }
    }

    fun mapEntityListToDomain(entities: List<NewsEntity>): List<News> {
        return entities.map { it.toDomain() }
    }

    fun mapDomainListToEntity(domains: List<News>): List<NewsEntity> {
        return domains.map { NewsEntity.fromDomain(it) }
    }
}