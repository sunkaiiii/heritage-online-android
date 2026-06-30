package com.duckylife.heritage.modern.feature.spacetime.model

import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.ContentTargetType
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension

/**
 * 时空探索筛选条件。
 */
data class SpacetimeFilters(
    val fromYear: Int? = null,
    val toYear: Int? = null,
    val region: String? = null,
    val category: String? = null,
    val kind: String? = null,
    val targetType: ContentTargetType? = null,
    val limit: Int = 20,
    val heatmapLimit: Int = 50,
) : java.io.Serializable {
    val isEmpty: Boolean
        get() = fromYear == null && toYear == null && region.isNullOrBlank() &&
            category.isNullOrBlank() && kind.isNullOrBlank() && targetType == null
}

data class SpacetimeOverviewUiModel(
    val total: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val topRegions: List<NamedCountUiModel> = emptyList(),
    val topCategories: List<NamedCountUiModel> = emptyList(),
    val yearTimeline: List<YearCountUiModel> = emptyList(),
    val generatedAt: String? = null,
)

data class SpacetimeMetricsUiModel(
    val total: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

data class NamedCountUiModel(
    val key: String,
    val label: String? = null,
    val count: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

data class YearCountUiModel(
    val year: Int,
    val count: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

data class SpacetimeHeatmapUiModel(
    val x: SpacetimeDimension = SpacetimeDimension.Unknown,
    val y: SpacetimeDimension = SpacetimeDimension.Unknown,
    val targetType: ContentTargetType? = null,
    val cells: List<SpacetimeHeatmapCellUiModel> = emptyList(),
    val generatedAt: String? = null,
)

data class SpacetimeHeatmapCellUiModel(
    val xKey: String,
    val xLabel: String? = null,
    val yKey: String,
    val yLabel: String? = null,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
    val intensity: Double = 0.0,
)

data class SpacetimeTimelineUiModel(
    val key: String,
    val label: String? = null,
    val buckets: List<YearCountUiModel> = emptyList(),
    val generatedAt: String? = null,
)

data class SpacetimeRegionMapUiModel(
    val year: Int,
    val regions: List<NamedCountUiModel> = emptyList(),
    val generatedAt: String? = null,
)

/**
 * 进一步分析 UI 模型。
 */
data class AnalyticsFacetsUiModel(
    val regions: List<AnalyticsFacetBucketUiModel> = emptyList(),
    val categories: List<AnalyticsFacetBucketUiModel> = emptyList(),
    val years: List<AnalyticsFacetBucketUiModel> = emptyList(),
    val kinds: List<AnalyticsFacetBucketUiModel> = emptyList(),
    val targetTypes: List<AnalyticsFacetBucketUiModel> = emptyList(),
    val generatedAt: String? = null,
)

data class AnalyticsFacetBucketUiModel(
    val key: String,
    val label: String? = null,
    val count: Int = 0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
)

data class AnalyticsBreakdownUiModel(
    val groupBy: AnalyticsDimension = AnalyticsDimension.Unknown,
    val targetType: ContentTargetType? = null,
    val buckets: List<Bucket> = emptyList(),
    val total: Int = 0,
    val generatedAt: String? = null,
) {
    data class Bucket(
        val key: String,
        val label: String? = null,
        val articleCount: Int = 0,
        val directoryItemCount: Int = 0,
        val inheritorCount: Int = 0,
        val total: Int = 0,
    )
}

data class AnalyticsCrosstabUiModel(
    val x: AnalyticsDimension = AnalyticsDimension.Unknown,
    val y: AnalyticsDimension = AnalyticsDimension.Unknown,
    val targetType: ContentTargetType? = null,
    val cells: List<Cell> = emptyList(),
    val xBuckets: List<String> = emptyList(),
    val yBuckets: List<String> = emptyList(),
    val total: Int = 0,
    val generatedAt: String? = null,
) {
    data class Cell(
        val xKey: String,
        val xLabel: String? = null,
        val yKey: String,
        val yLabel: String? = null,
        val articleCount: Int = 0,
        val directoryItemCount: Int = 0,
        val inheritorCount: Int = 0,
        val total: Int = 0,
    )
}

data class AnalyticsCompareUiModel(
    val dimension: AnalyticsDimension = AnalyticsDimension.Unknown,
    val metric: RankingMetric = RankingMetric.Total,
    val items: List<AnalyticsCompareItemUiModel> = emptyList(),
    val winnerKey: String? = null,
    val generatedAt: String? = null,
)

data class AnalyticsCompareItemUiModel(
    val key: String,
    val label: String? = null,
    val value: Double = 0.0,
    val articleCount: Int = 0,
    val directoryItemCount: Int = 0,
    val inheritorCount: Int = 0,
    val total: Int = 0,
)

data class AnalyticsOutlierUiModel(
    val dimension: AnalyticsDimension = AnalyticsDimension.Unknown,
    val key: String,
    val label: String? = null,
    val metric: RankingMetric = RankingMetric.Total,
    val value: Double = 0.0,
    val average: Double = 0.0,
    val ratioToAverage: Double = 0.0,
    val reason: String? = null,
)
