package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import com.duckylife.heritage.modern.core.saved.SavedContentType
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [
        com.duckylife.heritage.modern.di.DataModule::class,
        com.duckylife.heritage.modern.di.SavedContentModule::class,
    ],
)
object TestFakeDataModule {
    @Provides
    @Singleton
    fun provideHeritageRepository(): HeritageRepository = TestFakeRepository()

    @Provides
    @Singleton
    fun provideSavedContentRepository(): SavedContentRepository = TestFakeSavedContentRepository()
}

private class TestFakeSavedContentRepository : SavedContentRepository {
    private val entities = MutableStateFlow(mapOf<String, SavedContentEntity>())

    override fun observeFavoriteState(target: SavedContentTarget): Flow<Boolean> {
        val key = SavedContentRepository.computeKey(target)
        return entities.map { it[key]?.isFavorite == true }
    }

    override suspend fun toggleFavorite(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = entities.value[key]
        val now = System.currentTimeMillis()
        if (existing?.isFavorite == true) {
            entities.value = entities.value + (key to existing.copy(isFavorite = false, favoritedAt = null))
        } else {
            entities.value = entities.value + (key to SavedContentEntity(
                contentKey = key,
                contentType = snapshot.contentType.wireName,
                title = snapshot.title,
                summary = snapshot.summary,
                coverImageJson = snapshot.coverImageJson,
                category = snapshot.category,
                region = snapshot.region,
                year = snapshot.year,
                sourceUrl = snapshot.sourceUrl,
                targetId = snapshot.target.id,
                targetSourceId = snapshot.target.sourceId,
                targetSourceUrl = snapshot.target.sourceUrl,
                targetCategory = snapshot.target.category,
                targetKind = snapshot.target.kind,
                isFavorite = true,
                favoritedAt = now,
                lastViewedAt = now,
            ))
        }
    }

    override suspend fun recordViewed(snapshot: SavedContentSnapshot) {}

    override fun favorites(): Flow<List<SavedContentEntity>> =
        entities.map { it.values.filter { e -> e.isFavorite } }

    override fun recentlyViewed(): Flow<List<SavedContentEntity>> = flowOf(emptyList())
}
