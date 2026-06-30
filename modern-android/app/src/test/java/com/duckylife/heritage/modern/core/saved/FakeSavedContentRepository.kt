package com.duckylife.heritage.modern.core.saved

import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeSavedContentRepository : SavedContentRepository {
    private val entities = MutableStateFlow<Map<String, SavedContentEntity>>(emptyMap())

    val allEntities: Map<String, SavedContentEntity> get() = entities.value

    override fun observeFavoriteState(target: SavedContentTarget): Flow<Boolean> {
        val key = SavedContentRepository.computeKey(target)
        return entities.map { it[key]?.isFavorite == true }
    }

    override suspend fun toggleFavorite(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = entities.value[key]
        if (existing?.isFavorite == true) {
            entities.value = entities.value.toMutableMap().apply {
                this[key] = existing.copy(isFavorite = false, favoritedAt = null)
            }
        } else {
            val now = currentTimeMillis()
            entities.value = entities.value.toMutableMap().apply {
                this[key] = (existing ?: snapshot.toEntity(key, isFavorite = true, favoritedAt = now, lastViewedAt = now))
                    .copy(isFavorite = true, favoritedAt = now)
            }
        }
    }

    override suspend fun recordViewed(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = entities.value[key]
        val now = currentTimeMillis()
        val newEntity = (existing ?: snapshot.toEntity(
            key = key,
            isFavorite = existing?.isFavorite == true,
            favoritedAt = existing?.favoritedAt,
            lastViewedAt = now,
        )).copy(lastViewedAt = now)
        entities.value = entities.value.toMutableMap().apply {
            this[key] = newEntity
        }
    }

    override fun favorites(): Flow<List<SavedContentEntity>> =
        entities.map { map -> map.values.filter { it.isFavorite }.sortedByDescending { it.favoritedAt ?: 0 } }

    override fun recentlyViewed(): Flow<List<SavedContentEntity>> =
        entities.map { map -> map.values.filter { it.lastViewedAt > 0L }.sortedByDescending { it.lastViewedAt } }

    override suspend fun removeFavorite(target: SavedContentTarget) {
        val key = SavedContentRepository.computeKey(target)
        val existing = entities.value[key] ?: return
        entities.value = entities.value + (key to existing.copy(isFavorite = false, favoritedAt = null))
    }

    override suspend fun removeRecent(target: SavedContentTarget) {
        val key = SavedContentRepository.computeKey(target)
        val existing = entities.value[key] ?: return
        entities.value = entities.value + (key to existing.copy(lastViewedAt = 0L))
    }

    override suspend fun clearRecent() {
        entities.value = entities.value.mapValues { (_, entity) ->
            if (!entity.isFavorite) entity.copy(lastViewedAt = 0L) else entity
        }
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

    private fun currentTimeMillis(): Long = System.currentTimeMillis()
}
