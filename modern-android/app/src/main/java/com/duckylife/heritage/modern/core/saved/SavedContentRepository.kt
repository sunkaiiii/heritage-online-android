package com.duckylife.heritage.modern.core.saved

import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import kotlinx.coroutines.flow.Flow

interface SavedContentRepository {
    fun observeFavoriteState(target: SavedContentTarget): Flow<Boolean>

    suspend fun toggleFavorite(snapshot: SavedContentSnapshot)

    suspend fun recordViewed(snapshot: SavedContentSnapshot)

    fun favorites(): Flow<List<SavedContentEntity>>

    fun recentlyViewed(): Flow<List<SavedContentEntity>>

    suspend fun removeFavorite(target: SavedContentTarget)

    suspend fun removeRecent(target: SavedContentTarget)

    suspend fun clearRecent()

    companion object {
        fun computeKey(target: SavedContentTarget): String =
            target.id
                ?: target.sourceUrl
                ?: target.sourceId
                ?: error("Cannot compute saved content key")

        fun computeKey(snapshot: SavedContentSnapshot): String =
            snapshot.id
                ?: snapshot.sourceUrl
                ?: snapshot.target.sourceId
                ?: error("Cannot compute saved content key")
    }
}
