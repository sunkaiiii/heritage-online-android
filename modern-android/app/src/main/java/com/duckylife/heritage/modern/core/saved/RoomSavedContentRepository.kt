package com.duckylife.heritage.modern.core.saved

import com.duckylife.heritage.modern.core.database.HeritageDatabase
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomSavedContentRepository @Inject constructor(
    private val database: HeritageDatabase,
) : SavedContentRepository {
    private val dao get() = database.savedContentDao()

    override fun observeFavoriteState(target: SavedContentTarget): Flow<Boolean> {
        val key = SavedContentRepository.computeKey(target)
        return dao.observeByKey(key).map { it?.isFavorite == true }
    }

    override suspend fun toggleFavorite(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = dao.queryByKey(key)
        if (existing?.isFavorite == true) {
            dao.removeFavorite(key)
        } else {
            val now = currentTimeMillis()
            dao.upsert(
                (existing ?: snapshot.toEntity(key = key, isFavorite = true, favoritedAt = now, lastViewedAt = now))
                    .copy(isFavorite = true, favoritedAt = now),
            )
        }
    }

    override suspend fun recordViewed(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = dao.queryByKey(key)
        val now = currentTimeMillis()
        dao.upsert(
            (existing ?: snapshot.toEntity(
                key = key,
                isFavorite = false,
                favoritedAt = null,
                lastViewedAt = now,
            )).copy(lastViewedAt = now),
        )
        dao.trimToKeep(MAX_RECENT)
    }

    override fun favorites(): Flow<List<SavedContentEntity>> = dao.observeFavorites()

    override fun recentlyViewed(): Flow<List<SavedContentEntity>> = dao.observeRecentlyViewed()

    override suspend fun removeFavorite(target: SavedContentTarget) {
        dao.removeFavorite(SavedContentRepository.computeKey(target))
    }

    override suspend fun removeRecent(target: SavedContentTarget) {
        dao.removeRecent(SavedContentRepository.computeKey(target))
    }

    override suspend fun clearRecent() {
        dao.clearRecent()
    }

    private fun SavedContentSnapshot.toEntity(
        key: String,
        isFavorite: Boolean,
        favoritedAt: Long?,
        lastViewedAt: Long,
    ) = SavedContentEntity(
        contentKey = key,
        contentType = contentType.wireName,
        title = title,
        summary = summary,
        coverImageJson = coverImageJson,
        category = category,
        region = region,
        year = year,
        sourceUrl = sourceUrl,
        targetId = target.id,
        targetSourceId = target.sourceId,
        targetSourceUrl = target.sourceUrl,
        targetCategory = target.category,
        targetKind = target.kind,
        isFavorite = isFavorite,
        favoritedAt = favoritedAt,
        lastViewedAt = lastViewedAt,
    )

    companion object {
        private const val MAX_RECENT = 100
    }
}

private fun currentTimeMillis(): Long = System.currentTimeMillis()
