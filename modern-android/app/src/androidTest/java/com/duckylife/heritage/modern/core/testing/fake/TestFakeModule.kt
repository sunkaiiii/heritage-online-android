package com.duckylife.heritage.modern.core.testing.fake

import com.duckylife.heritage.modern.core.data.ContentExportRepository
import com.duckylife.heritage.modern.core.data.ContentIntelligenceRepository
import com.duckylife.heritage.modern.core.data.DataExploreRepository
import com.duckylife.heritage.modern.core.data.HeritageRepository
import com.duckylife.heritage.modern.core.data.IntelligentSearchRepository
import com.duckylife.heritage.modern.core.data.KnowledgeGraphRepository
import com.duckylife.heritage.modern.core.data.LearningRoutesRepository
import com.duckylife.heritage.modern.core.data.RecentContentProvider
import com.duckylife.heritage.modern.core.data.RecentContentRef
import com.duckylife.heritage.modern.core.data.ResearchRepository
import com.duckylife.heritage.modern.core.database.entity.SavedContentEntity
import com.duckylife.heritage.modern.core.network.IntelligentSearchQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportContentResultDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportPreviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportRequestDto
import com.duckylife.heritage.modern.core.network.dto.advanced.ExportTemplateDto
import com.duckylife.heritage.modern.core.network.dto.advanced.IntelligentSearchResponseDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.ResearchTaskStatus
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import com.duckylife.heritage.modern.core.saved.SavedContentRepository
import com.duckylife.heritage.modern.core.saved.SavedContentSnapshot
import com.duckylife.heritage.modern.core.saved.SavedContentTarget
import com.duckylife.heritage.modern.core.saved.SavedContentType
import com.duckylife.heritage.modern.feature.detail.intelligence.ContentIntelligenceViewModelDelegateFactory
import com.duckylife.heritage.modern.feature.detail.intelligence.DefaultContentIntelligenceViewModelDelegateFactory
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteNextUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageDetailUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchPackageItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportDetailUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchReportItemUiModel
import com.duckylife.heritage.modern.feature.research.model.ResearchSourceType
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsBreakdownUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCrosstabUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsOutlierUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeOverviewUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeRegionMapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeTimelineUiModel
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

    @Provides
    @Singleton
    fun provideContentExportRepository(): ContentExportRepository = TestFakeContentExportRepository()

    @Provides
    @Singleton
    fun provideLearningRoutesRepository(): LearningRoutesRepository = TestFakeLearningRoutesRepository()

    @Provides
    @Singleton
    fun provideDataExploreRepository(): DataExploreRepository = TestFakeDataExploreRepository()

    @Provides
    @Singleton
    fun provideResearchRepository(): ResearchRepository = TestFakeResearchRepository()
}

private class TestFakeContentExportRepository : ContentExportRepository {
    override suspend fun getTemplates(): List<ExportTemplateDto> = emptyList()
    override suspend fun previewExport(request: ExportRequestDto): ExportPreviewDto = ExportPreviewDto()
    override suspend fun exportContent(request: ExportRequestDto): ExportContentResultDto =
        ExportContentResultDto()
}

private class TestFakeLearningRoutesRepository : LearningRoutesRepository {
    override suspend fun getRoutes(
        difficulty: LearningRouteDifficulty,
        limit: Int,
    ): List<LearningRouteSummaryUiModel> = emptyList()

    override suspend fun getRouteDetail(
        routeId: String,
        limit: Int,
        includeAi: Boolean,
    ): LearningRouteDetailUiModel = emptyRouteDetail(routeId)

    override suspend fun buildRoute(
        seedType: com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType,
        seedKey: String,
        difficulty: LearningRouteDifficulty,
        limit: Int,
    ): LearningRouteDetailUiModel = emptyRouteDetail(seedKey)

    override suspend fun getNextStep(
        routeId: String,
        completedStepIds: List<String>,
    ): LearningRouteNextUiModel = LearningRouteNextUiModel(
        routeId = routeId,
        completed = false,
        nextStep = null,
        relatedRoutes = emptyList(),
    )

    private fun emptyRouteDetail(routeId: String): LearningRouteDetailUiModel = LearningRouteDetailUiModel(
        routeId = routeId,
        title = "",
        description = null,
        difficulty = LearningRouteDifficulty.All,
        estimatedMinutes = 0,
        sections = emptyList(),
        steps = emptyList(),
        relatedRoutes = emptyList(),
    )
}

private class TestFakeDataExploreRepository : DataExploreRepository {
    override suspend fun getSpacetimeOverview(filters: SpacetimeFilters): SpacetimeOverviewUiModel =
        SpacetimeOverviewUiModel()

    override suspend fun getSpacetimeHeatmap(
        x: com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension,
        y: com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension,
        filters: SpacetimeFilters,
    ): SpacetimeHeatmapUiModel = SpacetimeHeatmapUiModel(x = x, y = y)

    override suspend fun getRegionTimeline(region: String): SpacetimeTimelineUiModel =
        SpacetimeTimelineUiModel(key = region)

    override suspend fun getYearMap(year: Int): SpacetimeRegionMapUiModel =
        SpacetimeRegionMapUiModel(year = year)

    override suspend fun getCategoryTimeline(category: String): SpacetimeTimelineUiModel =
        SpacetimeTimelineUiModel(key = category)

    override suspend fun getAnalyticsFacets(
        filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
    ): AnalyticsFacetsUiModel = AnalyticsFacetsUiModel()

    override suspend fun getAnalyticsBreakdown(
        groupBy: com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension,
        filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
        limit: Int,
    ): AnalyticsBreakdownUiModel = AnalyticsBreakdownUiModel(groupBy = groupBy)

    override suspend fun getAnalyticsCrosstab(
        x: com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension,
        y: com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension,
        filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
        limit: Int,
    ): AnalyticsCrosstabUiModel = AnalyticsCrosstabUiModel(x = x, y = y)

    override suspend fun getAnalyticsCompare(
        dimension: com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension,
        keys: List<String>,
        metric: com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric,
        filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
    ): AnalyticsCompareUiModel = AnalyticsCompareUiModel(dimension = dimension, metric = metric)

    override suspend fun getAnalyticsOutliers(
        dimension: com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension,
        metric: com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric,
        filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
        limit: Int,
    ): List<AnalyticsOutlierUiModel> = emptyList()

    override suspend fun getRankings(): List<RankingDefinitionUiModel> = emptyList()

    override suspend fun getRankingDetail(
        rankingId: String,
        filters: RankingFilters,
    ): RankingDetailUiModel = RankingDetailUiModel(rankingId = rankingId, title = "")
}

private class TestFakeResearchRepository : ResearchRepository {
    override suspend fun getPackages(): List<ResearchPackageItemUiModel> = emptyList()
    override suspend fun getPackageDetail(packageId: String): ResearchPackageDetailUiModel =
        ResearchPackageDetailUiModel(
            packageId = packageId,
            title = packageId,
            querySummary = null,
            sourceType = ResearchSourceType.Unknown,
            sourceDetail = null,
            dataScope = emptyList(),
            createdAt = null,
            status = ResearchTaskStatus.Succeeded,
            nodeCount = 0,
            edgeCount = 0,
            sourceCount = 0,
            evidenceCount = 0,
            artifacts = emptyList(),
            hasReport = false,
            reportId = null,
            warnings = emptyList(),
        )

    override suspend fun getArtifactContent(packageId: String, artifactName: String): String = ""
    override suspend fun getReports(): List<ResearchReportItemUiModel> = emptyList()
    override suspend fun getReportDetail(reportId: String): ResearchReportDetailUiModel =
        ResearchReportDetailUiModel(
            reportId = reportId,
            packageId = null,
            title = reportId,
            status = ResearchTaskStatus.Succeeded,
            createdAt = null,
            executiveSummary = "",
            findings = emptyList(),
            sourceCount = 0,
            limitations = emptyList(),
            warnings = emptyList(),
            followUpQuestions = emptyList(),
        )

    override suspend fun getReportByPackage(packageId: String): ResearchReportDetailUiModel =
        getReportDetail(packageId)
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
