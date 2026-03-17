package com.example.smart_planner.data.database.news

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val abstractText: String,
    val source: String,
    val publishedDate: Long,
    val imageUrl: String?,
    val url: String,
    val cacheTimestamp: Long = System.currentTimeMillis()
)