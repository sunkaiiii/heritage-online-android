package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "article_details",
    indices = [
        Index(value = ["sourceId", "category"]),
        Index(value = ["sourceUrl", "category"]),
    ],
)
data class ArticleDetailEntity(
    @PrimaryKey val id: String,
    val sourceId: String?,
    val category: String,
    val title: String?,
    val summary: String?,
    val publishedAt: String?,
    val coverImageJson: String?,
    val sourceUrl: String?,
    val sourceName: String?,
    val author: String?,
    val editor: String?,
    val contentBlocksJson: String,
    val relatedArticlesJson: String,
    val updatedAtEpochMillis: Long,
)
