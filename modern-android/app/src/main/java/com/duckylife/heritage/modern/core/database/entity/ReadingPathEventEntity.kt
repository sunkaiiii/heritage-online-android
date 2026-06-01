package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_path_events")
data class ReadingPathEventEntity(
    @PrimaryKey val id: String,
    val fromType: String? = null,
    val fromId: String? = null,
    val fromTitle: String? = null,
    val toType: String,
    val toId: String,
    val toTitle: String? = null,
    val source: String,
    val createdAt: Long,
)
