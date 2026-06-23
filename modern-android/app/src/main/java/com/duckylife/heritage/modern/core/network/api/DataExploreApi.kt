package com.duckylife.heritage.modern.core.network.api

import com.duckylife.heritage.modern.core.network.AnalyticsBreakdownQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCompareQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCrosstabQuery
import com.duckylife.heritage.modern.core.network.AnalyticsFacetsQuery
import com.duckylife.heritage.modern.core.network.AnalyticsOutliersQuery
import com.duckylife.heritage.modern.core.network.RankingContentQuery
import com.duckylife.heritage.modern.core.network.RankingDetailQuery
import com.duckylife.heritage.modern.core.network.SpacetimeCategoryTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeHeatmapQuery
import com.duckylife.heritage.modern.core.network.SpacetimeOverviewQuery
import com.duckylife.heritage.modern.core.network.SpacetimeRegionTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeYearMapQuery
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCompareDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsCrosstabDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsFacetsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsOutliersDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDefinitionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeHeatmapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeRegionMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto

/**
 * 时空探索、分析与排行榜端点契约。
 */
interface DataExploreApi {
    suspend fun getSpacetimeOverview(query: SpacetimeOverviewQuery): SpacetimeOverviewDto

    suspend fun getSpacetimeHeatmap(query: SpacetimeHeatmapQuery): SpacetimeHeatmapDto

    suspend fun getSpacetimeRegionTimeline(query: SpacetimeRegionTimelineQuery): SpacetimeTimelineDto

    suspend fun getSpacetimeYearMap(query: SpacetimeYearMapQuery): SpacetimeRegionMapDto

    suspend fun getSpacetimeCategoryTimeline(query: SpacetimeCategoryTimelineQuery): SpacetimeTimelineDto

    suspend fun getAnalyticsFacets(query: AnalyticsFacetsQuery): AnalyticsFacetsDto

    suspend fun getAnalyticsBreakdown(query: AnalyticsBreakdownQuery): AnalyticsBreakdownDto

    suspend fun getAnalyticsCrosstab(query: AnalyticsCrosstabQuery): AnalyticsCrosstabDto

    suspend fun getAnalyticsCompare(query: AnalyticsCompareQuery): AnalyticsCompareDto

    suspend fun getAnalyticsOutliers(query: AnalyticsOutliersQuery): List<AnalyticsOutliersDto>

    suspend fun getRankings(): List<RankingDefinitionDto>

    suspend fun getRankingDetail(query: RankingDetailQuery): RankingDetailDto

    suspend fun getRankingContent(query: RankingContentQuery): RankingDetailDto
}
