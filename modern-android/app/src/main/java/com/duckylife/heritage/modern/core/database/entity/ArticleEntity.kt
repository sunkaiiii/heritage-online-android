package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: String,
    val queryKey: String,
    val category: String,
    val title: String?,
    val summary: String?,
    val publishedAt: String?,
    val coverImageJson: String?,
    val sourceUrl: String?,
    val page: Int,
    val positionInPage: Int,
)
