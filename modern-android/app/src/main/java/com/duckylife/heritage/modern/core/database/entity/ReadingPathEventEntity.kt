package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading_path_events",
    indices = [Index(value = ["createdAt"])],
)
data class ReadingPathEventEntity(
    @PrimaryKey val id: String,
    val fromType: String? = null,
    val fromId: String? = null,
    val fromTitle: String? = null,
    val toType: String,
    val toId: String,
    val toTitle: String? = null,
    val source: String,
    val toCategory: String? = null,
    val toKind: String? = null,
    val toSourceId: String? = null,
    val toSourceUrl: String? = null,
    val toSubtitle: String? = null,
    val toImageUrl: String? = null,
    val createdAt: Long,
)
