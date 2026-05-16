package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "directory_details",
    indices = [
        Index(value = ["sourceId", "kind"]),
    ],
)
data class DirectoryDetailEntity(
    @PrimaryKey val id: String,
    val sourceId: String?,
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
    val nominationType: String?,
    val protectionUnit: String?,
    val galleryJson: String,
    val contentBlocksJson: String,
    val relatedProjectsJson: String,
    val relatedInheritorsJson: String,
    val relatedDocumentsJson: String,
    val updatedAtEpochMillis: Long,
)
