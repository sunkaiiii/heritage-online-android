package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.AnalyticsBreakdownQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCompareQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCrosstabQuery
import com.duckylife.heritage.modern.core.network.AnalyticsFacetsQuery
import com.duckylife.heritage.modern.core.network.AnalyticsFilters
import com.duckylife.heritage.modern.core.network.AnalyticsOutliersQuery
import com.duckylife.heritage.modern.core.network.RankingContentQuery
import com.duckylife.heritage.modern.core.network.RankingDetailQuery
import com.duckylife.heritage.modern.core.network.SpacetimeCategoryTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeHeatmapQuery
import com.duckylife.heritage.modern.core.network.SpacetimeOverviewQuery
import com.duckylife.heritage.modern.core.network.SpacetimeRegionTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeYearMapQuery
import com.duckylife.heritage.modern.core.network.api.DataExploreApi
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCompareDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCrosstabDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsFacetBucketDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsFacetsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsOutliersDto
import com.duckylife.heritage.modern.core.network.dto.advanced.NamedCountDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDefinitionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingItemDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapCellDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeRegionMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto
import com.duckylife.heritage.modern.core.network.dto.advanced.YearCountDto
import com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.feature.rankings.model.RankingItemUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingMetricUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsBreakdownUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareItemUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCrosstabUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetBucketUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsOutlierUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.NamedCountUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapCellUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeMetricsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeOverviewUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeRegionMapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeTimelineUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.YearCountUiModel
import javax.inject.Inject

/**
 * 数据探索仓库：时空探索、进一步分析、排行榜。
 */
interface DataExploreRepository {
    suspend fun getSpacetimeOverview(filters: SpacetimeFilters): SpacetimeOverviewUiModel
    suspend fun getSpacetimeHeatmap(
        x: SpacetimeDimension,
        y: SpacetimeDimension,
        filters: SpacetimeFilters,
    ): SpacetimeHeatmapUiModel

    suspend fun getRegionTimeline(region: String): SpacetimeTimelineUiModel
    suspend fun getYearMap(year: Int): SpacetimeRegionMapUiModel
    suspend fun getCategoryTimeline(category: String): SpacetimeTimelineUiModel

    suspend fun getAnalyticsFacets(filters: AnalyticsFilters): AnalyticsFacetsUiModel
    suspend fun getAnalyticsBreakdown(
        groupBy: AnalyticsDimension,
        filters: AnalyticsFilters,
        limit: Int = 50,
    ): AnalyticsBreakdownUiModel

    suspend fun getAnalyticsCrosstab(
        x: AnalyticsDimension,
        y: AnalyticsDimension,
        filters: AnalyticsFilters,
        limit: Int = 100,
    ): AnalyticsCrosstabUiModel

    suspend fun getAnalyticsCompare(
        dimension: AnalyticsDimension,
        keys: List<String>,
        metric: RankingMetric,
        filters: AnalyticsFilters,
    ): AnalyticsCompareUiModel

    suspend fun getAnalyticsOutliers(
        dimension: AnalyticsDimension,
        metric: RankingMetric,
        filters: AnalyticsFilters,
        limit: Int = 20,
    ): List<AnalyticsOutlierUiModel>

    suspend fun getRankings(): List<RankingDefinitionUiModel>
    suspend fun getRankingDetail(rankingId: String, filters: RankingFilters): RankingDetailUiModel
    suspend fun getRankingContent(metric: RankingMetric, filters: RankingFilters): RankingDetailUiModel
}

class DefaultDataExploreRepository @Inject constructor(
    private val api: DataExploreApi,
) : DataExploreRepository {

    override suspend fun getSpacetimeOverview(filters: SpacetimeFilters): SpacetimeOverviewUiModel =
        api.getSpacetimeOverview(
            SpacetimeOverviewQuery(
                fromYear = filters.fromYear,
                toYear = filters.toYear,
                region = filters.region,
                category = filters.category,
                kind = filters.kind,
                targetType = filters.targetType,
                limit = filters.limit,
            ),
        ).toUiModel()

    override suspend fun getSpacetimeHeatmap(
        x: SpacetimeDimension,
        y: SpacetimeDimension,
        filters: SpacetimeFilters,
    ): SpacetimeHeatmapUiModel =
        api.getSpacetimeHeatmap(
            SpacetimeHeatmapQuery(
                x = x,
                y = y,
                targetType = filters.targetType,
                fromYear = filters.fromYear,
                toYear = filters.toYear,
                limit = filters.heatmapLimit,
            ),
        ).toUiModel()

    override suspend fun getRegionTimeline(region: String): SpacetimeTimelineUiModel =
        api.getSpacetimeRegionTimeline(SpacetimeRegionTimelineQuery(region = region)).toUiModel()

    override suspend fun getYearMap(year: Int): SpacetimeRegionMapUiModel =
        api.getSpacetimeYearMap(SpacetimeYearMapQuery(year = year)).toUiModel()

    override suspend fun getCategoryTimeline(category: String): SpacetimeTimelineUiModel =
        api.getSpacetimeCategoryTimeline(SpacetimeCategoryTimelineQuery(category = category)).toUiModel()

    override suspend fun getAnalyticsFacets(filters: AnalyticsFilters): AnalyticsFacetsUiModel =
        api.getAnalyticsFacets(AnalyticsFacetsQuery(filters = filters)).toUiModel()

    override suspend fun getAnalyticsBreakdown(
        groupBy: AnalyticsDimension,
        filters: AnalyticsFilters,
        limit: Int,
    ): AnalyticsBreakdownUiModel =
        api.getAnalyticsBreakdown(
            AnalyticsBreakdownQuery(groupBy = groupBy, limit = limit, filters = filters),
        ).toUiModel()

    override suspend fun getAnalyticsCrosstab(
        x: AnalyticsDimension,
        y: AnalyticsDimension,
        filters: AnalyticsFilters,
        limit: Int,
    ): AnalyticsCrosstabUiModel =
        api.getAnalyticsCrosstab(
            AnalyticsCrosstabQuery(x = x, y = y, limit = limit, filters = filters),
        ).toUiModel()

    override suspend fun getAnalyticsCompare(
        dimension: AnalyticsDimension,
        keys: List<String>,
        metric: RankingMetric,
        filters: AnalyticsFilters,
    ): AnalyticsCompareUiModel =
        api.getAnalyticsCompare(
            AnalyticsCompareQuery(
                dimension = dimension,
                keys = keys,
                metric = metric,
                filters = filters,
            ),
        ).toUiModel()

    override suspend fun getAnalyticsOutliers(
        dimension: AnalyticsDimension,
        metric: RankingMetric,
        filters: AnalyticsFilters,
        limit: Int,
    ): List<AnalyticsOutlierUiModel> =
        api.getAnalyticsOutliers(
            AnalyticsOutliersQuery(
                dimension = dimension,
                metric = metric,
                limit = limit,
                filters = filters,
            ),
        ).map { it.toUiModel() }

    override suspend fun getRankings(): List<RankingDefinitionUiModel> =
        api.getRankings().map { it.toUiModel() }

    override suspend fun getRankingDetail(rankingId: String, filters: RankingFilters): RankingDetailUiModel =
        api.getRankingDetail(
            RankingDetailQuery(
                rankingId = rankingId,
                targetType = filters.targetType,
                region = filters.region,
                category = filters.category,
                year = filters.year,
                limit = filters.limit,
            ),
        ).toUiModel()

    override suspend fun getRankingContent(metric: RankingMetric, filters: RankingFilters): RankingDetailUiModel =
        api.getRankingContent(
            RankingContentQuery(
                metric = metric,
                targetType = filters.targetType,
                region = filters.region,
                category = filters.category,
                year = filters.year,
                limit = filters.limit,
            ),
        ).toUiModel()
}

// ---------------------------------------------------------------------------
// Spacetime mappers
// ---------------------------------------------------------------------------

internal fun SpacetimeOverviewDto.toUiModel(): SpacetimeOverviewUiModel =
    SpacetimeOverviewUiModel(
        total = metrics?.total ?: 0,
        articleCount = metrics?.articleCount ?: 0,
        directoryItemCount = metrics?.directoryItemCount ?: 0,
        inheritorCount = metrics?.inheritorCount ?: 0,
        topRegions = topRegions.map { it.toUiModel() },
        topCategories = topCategories.map { it.toUiModel() },
        yearTimeline = yearTimeline.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun SpacetimeHeatmapDto.toUiModel(): SpacetimeHeatmapUiModel =
    SpacetimeHeatmapUiModel(
        x = x,
        y = y,
        targetType = targetType,
        cells = cells.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun SpacetimeTimelineDto.toUiModel(): SpacetimeTimelineUiModel =
    SpacetimeTimelineUiModel(
        key = key.orEmpty(),
        label = label,
        buckets = buckets.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun SpacetimeRegionMapDto.toUiModel(): SpacetimeRegionMapUiModel =
    SpacetimeRegionMapUiModel(
        year = year,
        regions = regions.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun NamedCountDto.toUiModel(): NamedCountUiModel =
    NamedCountUiModel(
        key = displayKey,
        label = displayLabel,
        count = displayCount,
        articleCount = articleCount,
        directoryItemCount = directoryItemCount,
        inheritorCount = inheritorCount,
    )

internal fun YearCountDto.toUiModel(): YearCountUiModel =
    YearCountUiModel(
        year = year,
        count = count.takeIf { it != 0 } ?: total,
        articleCount = articleCount,
        directoryItemCount = directoryItemCount,
        inheritorCount = inheritorCount,
    )

private val NamedCountDto.displayKey: String
    get() = key
        ?.takeIf { it.isNotBlank() }
        ?: regionKey?.takeIf { it.isNotBlank() }
        ?: categoryKey?.takeIf { it.isNotBlank() }
        ?: kindKey?.takeIf { it.isNotBlank() }
        ?: targetType?.wireName?.takeIf { it.isNotBlank() }
        ?: ""

private val NamedCountDto.displayLabel: String?
    get() = label
        ?.takeIf { it.isNotBlank() }
        ?: regionLabel?.takeIf { it.isNotBlank() }
        ?: categoryLabel?.takeIf { it.isNotBlank() }
        ?: kindLabel?.takeIf { it.isNotBlank() }
        ?: targetTypeLabel?.takeIf { it.isNotBlank() }
        ?: displayKey.takeIf { it.isNotBlank() }

private val NamedCountDto.displayCount: Int
    get() = count.takeIf { it != 0 } ?: total

internal fun SpacetimeHeatmapCellDto.toUiModel(): SpacetimeHeatmapCellUiModel =
    SpacetimeHeatmapCellUiModel(
        xKey = xKey,
        xLabel = xLabel,
        yKey = yKey,
        yLabel = yLabel,
        articleCount = articleCount,
        directoryItemCount = directoryItemCount,
        inheritorCount = inheritorCount,
        total = total,
        intensity = intensity,
    )

// ---------------------------------------------------------------------------
// Analytics mappers
// ---------------------------------------------------------------------------

internal fun AnalyticsFacetsDto.toUiModel(): AnalyticsFacetsUiModel =
    AnalyticsFacetsUiModel(
        regions = regions.map { it.toUiModel() },
        categories = categories.map { it.toUiModel() },
        years = years.map { it.toUiModel() },
        kinds = kinds.map { it.toUiModel() },
        targetTypes = targetTypes.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun AnalyticsFacetBucketDto.toUiModel(): AnalyticsFacetBucketUiModel =
    AnalyticsFacetBucketUiModel(
        key = key,
        label = label,
        count = count,
        articleCount = articleCount,
        directoryItemCount = directoryItemCount,
        inheritorCount = inheritorCount,
    )

internal fun AnalyticsBreakdownDto.toUiModel(): AnalyticsBreakdownUiModel =
    AnalyticsBreakdownUiModel(
        groupBy = groupBy,
        targetType = targetType,
        buckets = buckets.map { bucket ->
            AnalyticsBreakdownUiModel.Bucket(
                key = bucket.key,
                label = bucket.label,
                articleCount = bucket.articleCount,
                directoryItemCount = bucket.directoryItemCount,
                inheritorCount = bucket.inheritorCount,
                total = bucket.total,
            )
        },
        total = total,
        generatedAt = generatedAt,
    )

internal fun AnalyticsCrosstabDto.toUiModel(): AnalyticsCrosstabUiModel =
    AnalyticsCrosstabUiModel(
        x = x,
        y = y,
        targetType = targetType,
        cells = cells.map { cell ->
            AnalyticsCrosstabUiModel.Cell(
                xKey = cell.xKey,
                xLabel = cell.xLabel,
                yKey = cell.yKey,
                yLabel = cell.yLabel,
                articleCount = cell.articleCount,
                directoryItemCount = cell.directoryItemCount,
                inheritorCount = cell.inheritorCount,
                total = cell.total,
            )
        },
        xBuckets = xBuckets,
        yBuckets = yBuckets,
        total = total,
        generatedAt = generatedAt,
    )

internal fun AnalyticsCompareDto.toUiModel(): AnalyticsCompareUiModel =
    AnalyticsCompareUiModel(
        dimension = dimension,
        metric = metric,
        items = items.map { item ->
            AnalyticsCompareItemUiModel(
                key = item.key,
                label = item.label,
                value = item.value,
                articleCount = item.articleCount,
                directoryItemCount = item.directoryItemCount,
                inheritorCount = item.inheritorCount,
                total = item.total,
            )
        },
        winnerKey = winnerKey,
        generatedAt = generatedAt,
    )

internal fun AnalyticsOutliersDto.toUiModel(): AnalyticsOutlierUiModel =
    AnalyticsOutlierUiModel(
        dimension = dimension,
        key = key,
        label = label,
        metric = metric,
        value = value,
        average = average,
        ratioToAverage = ratioToAverage,
        reason = reason,
    )

// ---------------------------------------------------------------------------
// Rankings mappers
// ---------------------------------------------------------------------------

internal fun RankingDefinitionDto.toUiModel(): RankingDefinitionUiModel =
    RankingDefinitionUiModel(
        rankingId = rankingId,
        title = title.orEmpty(),
        description = description,
        metric = metric,
        targetType = targetType,
        refreshHint = refreshHint,
    )

internal fun RankingDetailDto.toUiModel(): RankingDetailUiModel =
    RankingDetailUiModel(
        rankingId = rankingId,
        title = title.orEmpty(),
        description = description,
        metric = metric,
        items = items.map { it.toUiModel() },
        generatedAt = generatedAt,
    )

internal fun RankingItemDto.toUiModel(): RankingItemUiModel =
    RankingItemUiModel(
        rank = rank,
        targetType = targetType,
        targetId = targetId,
        title = title.orEmpty(),
        subtitle = subtitle,
        score = score,
        metrics = metrics.map { it.toUiModel() },
        reasons = reasons,
        contentId = content?.id,
        contentType = content?.type?.wireName,
    )

internal fun com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetricDto.toUiModel(): RankingMetricUiModel =
    RankingMetricUiModel(
        key = key,
        label = label,
        value = value,
        weight = weight,
    )
