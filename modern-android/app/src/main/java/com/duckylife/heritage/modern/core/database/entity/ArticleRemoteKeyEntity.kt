package com.duckylife.heritage.modern.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "article_remote_keys")
data class ArticleRemoteKeyEntity(
    @PrimaryKey val queryKey: String,
    val nextPage: Int?,
    val hasMore: Boolean,
)
