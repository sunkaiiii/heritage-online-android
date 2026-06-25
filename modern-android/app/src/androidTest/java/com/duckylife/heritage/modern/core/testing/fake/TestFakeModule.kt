package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.ContentIntelligenceRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.data.IntelligentSearchRepository
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.data.RecentContentProvider
import com.duckylife.heritage.modern.core.data.RecentContentRef
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceViewModelDelegateFactory
import com.duckylife.heritage.modern.feature.detail.intelligence.DefaultContentIntelligenceViewModelDelegateFactory
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

private val FAKE_PROFILE_ID = "android_test_profile"

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

    @Provides
    @Singleton
    fun provideContentIntelligenceRepository(): ContentIntelligenceRepository =
        TestFakeContentIntelligenceRepository()

    @Provides
    @Singleton
    fun provideIntelligentSearchRepository(): IntelligentSearchRepository =
        object : IntelligentSearchRepository {
            override suspend fun search(query: IntelligentSearchQuery): IntelligentSearchResponseDto =
                IntelligentSearchResponseDto(query = query.keywords)
        }

    @Provides
    @Singleton
    fun provideContentIntelligenceViewModelDelegateFactory(
        repository: ContentIntelligenceRepository,
    ): ContentIntelligenceViewModelDelegateFactory =
        DefaultContentIntelligenceViewModelDelegateFactory(repository)

    @Provides
    @Singleton
    fun provideKnowledgeGraphRepository(): KnowledgeGraphRepository =
        TestFakeKnowledgeGraphRepository()

    @Provides
    @Singleton
    fun provideRecentContentProvider(): RecentContentProvider = object : RecentContentProvider {
        override fun observeRecentContent(): kotlinx.coroutines.flow.Flow<RecentContentRef?> =
            kotlinx.coroutines.flow.flowOf(null)
    }

    @Provides
    @Singleton
    fun provideLocalProfileRepository(): LocalProfileRepository = object : LocalProfileRepository {
        override val profileId: Flow<String> = flowOf(FAKE_PROFILE_ID)
        override suspend fun currentProfileId(): String = FAKE_PROFILE_ID
    }
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

    override suspend fun recordViewed(snapshot: SavedContentSnapshot) {
        val key = SavedContentRepository.computeKey(snapshot)
        val existing = entities.value[key]
        val now = System.currentTimeMillis()
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
            isFavorite = existing?.isFavorite ?: false,
            favoritedAt = existing?.favoritedAt,
            lastViewedAt = now,
        ))
    }

    override fun favorites(): Flow<List<SavedContentEntity>> =
        entities.map { it.values.filter { e -> e.isFavorite } }

    override fun recentlyViewed(): Flow<List<SavedContentEntity>> =
        entities.map { it.values.filter { e -> e.lastViewedAt > 0L }.sortedByDescending { e -> e.lastViewedAt } }

    override suspend fun removeFavorite(target: SavedContentTarget) {
        val key = SavedContentRepository.computeKey(target)
        entities.value = entities.value + (key to (entities.value[key]?.copy(isFavorite = false, favoritedAt = null) ?: return))
    }

    override suspend fun removeRecent(target: SavedContentTarget) {
        val key = SavedContentRepository.computeKey(target)
        entities.value = entities.value + (key to (entities.value[key]?.copy(lastViewedAt = 0L) ?: return))
    }

    override suspend fun clearRecent() {
        entities.value = entities.value.mapValues { (_, entity) ->
            if (!entity.isFavorite) entity.copy(lastViewedAt = 0L) else entity
        }
    }
}
