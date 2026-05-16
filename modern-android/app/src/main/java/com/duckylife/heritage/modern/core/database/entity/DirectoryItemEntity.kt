package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "directory_items")
data class DirectoryItemEntity(
    @PrimaryKey val id: String,
    val queryKey: String,
    val kind: String,
    val title: String?,
    val summary: String?,
    val category: String?,
    val region: String?,
    val projectCode: String?,
    val batch: String?,
    val publishedYear: Int?,
    val listType: String?,
    val coverImageJson: String?,
    val sourceUrl: String?,
    val page: Int,
    val positionInPage: Int,
)
