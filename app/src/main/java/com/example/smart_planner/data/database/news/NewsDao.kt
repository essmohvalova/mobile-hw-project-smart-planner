package com.example.smart_planner.data.database.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NewsDao {
    @Query("SELECT * FROM news ORDER BY publishedDate DESC")
    fun getAllNews(): Flow<List<NewsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Query("DELETE FROM news WHERE cacheTimestamp < :expiryTime")
    suspend fun deleteExpired(expiryTime: Long)

    @Query("DELETE FROM news")
    suspend fun clearAll()
}