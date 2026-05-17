package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inheritor_details",
    indices = [
        Index(value = ["sourceId"]),
    ],
)
data class InheritorDetailEntity(
    @PrimaryKey val id: String,
    val sourceId: String?,
    val name: String?,
    val gender: String?,
    val birthDateText: String?,
    val ethnicity: String?,
    val category: String?,
    val projectCode: String?,
    val projectName: String?,
    val region: String?,
    val batch: String?,
    val description: String?,
    val coverImageJson: String?,
    val sourceUrl: String?,
    val contentBlocksJson: String,
    val relatedProjectsJson: String,
    val relatedInheritorsJson: String,
    val updatedAtEpochMillis: Long,
)
