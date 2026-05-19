package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_content")
data class SavedContentEntity(
    @PrimaryKey val contentKey: String,
    val contentType: String,
    val title: String?,
    val summary: String?,
    val coverImageJson: String?,
    val category: String?,
    val region: String?,
    val year: Int?,
    val sourceUrl: String?,
    val targetId: String?,
    val targetSourceId: String?,
    val targetSourceUrl: String?,
    val targetCategory: String?,
    val targetKind: String?,
    val isFavorite: Boolean,
    val favoritedAt: Long?,
    val lastViewedAt: Long,
)
