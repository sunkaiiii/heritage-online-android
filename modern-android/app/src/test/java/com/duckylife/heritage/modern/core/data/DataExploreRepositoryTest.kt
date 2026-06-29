package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.AnalyticsBreakdownQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCompareQuery
import com.duckylife.heritage.modern.core.network.AnalyticsCrosstabQuery
import com.duckylife.heritage.modern.core.network.AnalyticsFacetsQuery
import com.duckylife.heritage.modern.core.network.AnalyticsFilters
import com.duckylife.heritage.modern.core.network.AnalyticsOutliersQuery
import com.duckylife.heritage.modern.core.network.RankingDetailQuery
import com.duckylife.heritage.modern.core.network.SpacetimeCategoryTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeHeatmapQuery
import com.duckylife.heritage.modern.core.network.SpacetimeOverviewQuery
import com.duckylife.heritage.modern.core.network.SpacetimeRegionTimelineQuery
import com.duckylife.heritage.modern.core.network.SpacetimeYearMapQuery
import com.duckylife.heritage.modern.core.network.api.DataExploreApi
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownDto
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsBreakdownBucketDto
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
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeMetricsDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeOverviewDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeRegionMapDto
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeTimelineDto
import com.duckylife.heritage.modern.core.network.dto.advanced.YearCountDto
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class DataExploreRepositoryTest {

    private val fakeApi = FakeDataExploreApi()
    private val repository: DataExploreRepository = DefaultDataExploreRepository(api = fakeApi)

    @Test
    fun `getSpacetimeOverview maps metrics and top lists`() = runTest {
        fakeApi.overviewResult = SpacetimeOverviewDto(
            metrics = SpacetimeMetricsDto(total = 10, articleCount = 4, directoryItemCount = 4, inheritorCount = 2),
            topRegions = listOf(NamedCountDto(key = "浙江", label = "浙江", count = 3)),
            topCategories = listOf(NamedCountDto(key = "传统技艺", label = "传统技艺", count = 2)),
            yearTimeline = listOf(YearCountDto(year = 2024, count = 1)),
        )

        val result = repository.getSpacetimeOverview(
            SpacetimeFilters(fromYear = 2006, toYear = 2024, region = "浙江"),
        )

        assertEquals(10, result.total)
        assertEquals(4, result.articleCount)
        assertEquals(2, result.inheritorCount)
        assertEquals(1, result.topRegions.size)
        assertEquals("浙江", result.topRegions.first().key)
        assertEquals(1, result.yearTimeline.size)
        with(fakeApi.capturedOverviewQuery) {
            assertEquals(2006, this?.fromYear)
            assertEquals(2024, this?.toYear)
            assertEquals("浙江", this?.region)
        }
    }

    @Test
    fun `getSpacetimeOverview maps backend aliases for regions and totals`() = runTest {
        fakeApi.overviewResult = SpacetimeOverviewDto(
            metrics = SpacetimeMetricsDto(total = 60, articleCount = 10, directoryItemCount = 20, inheritorCount = 30),
            topRegions = listOf(
                NamedCountDto(
                    regionKey = "浙江",
                    regionLabel = "浙江",
                    total = 5,
                    directoryItemCount = 2,
                    inheritorCount = 3,
                ),
            ),
            topCategories = listOf(NamedCountDto(key = "specialtopic", label = "specialtopic", total = 4)),
            yearTimeline = listOf(YearCountDto(year = 2006, total = 6)),
        )

        val result = repository.getSpacetimeOverview(SpacetimeFilters())

        assertEquals(60, result.total)
        assertEquals("浙江", result.topRegions.first().key)
        assertEquals("浙江", result.topRegions.first().label)
        assertEquals(5, result.topRegions.first().count)
        assertEquals(4, result.topCategories.first().count)
        assertEquals(6, result.yearTimeline.first().count)
    }

    @Test
    fun `getSpacetimeHeatmap passes dimensions and filters`() = runTest {
        fakeApi.heatmapResult = SpacetimeHeatmapDto(
            x = SpacetimeDimension.Region,
            y = SpacetimeDimension.Category,
            cells = listOf(
                SpacetimeHeatmapCellDto(
                    xKey = "浙江",
                    yKey = "传统技艺",
                    total = 5,
                    intensity = 0.8,
                ),
            ),
        )

        val result = repository.getSpacetimeHeatmap(
            x = SpacetimeDimension.Region,
            y = SpacetimeDimension.Category,
            filters = SpacetimeFilters(targetType = "all"),
        )

        assertEquals(SpacetimeDimension.Region, result.x)
        assertEquals(SpacetimeDimension.Category, result.y)
        assertEquals(1, result.cells.size)
        assertEquals("浙江", result.cells.first().xKey)
        with(fakeApi.capturedHeatmapQuery) {
            assertEquals(SpacetimeDimension.Region, this?.x)
            assertEquals(SpacetimeDimension.Category, this?.y)
        }
    }

    @Test
    fun `getRegionTimeline maps key and buckets`() = runTest {
        fakeApi.regionTimelineResult = SpacetimeTimelineDto(
            key = "浙江",
            label = "浙江",
            buckets = listOf(YearCountDto(year = 2024, count = 2)),
        )

        val result = repository.getRegionTimeline("浙江")

        assertEquals("浙江", result.key)
        assertEquals(1, result.buckets.size)
        assertEquals(fakeApi.capturedRegionTimelineQuery?.region, "浙江")
    }

    @Test
    fun `getYearMap maps year and regions`() = runTest {
        fakeApi.yearMapResult = SpacetimeRegionMapDto(
            year = 2024,
            regions = listOf(NamedCountDto(key = "浙江", count = 2)),
        )

        val result = repository.getYearMap(2024)

        assertEquals(2024, result.year)
        assertEquals(1, result.regions.size)
        assertEquals(fakeApi.capturedYearMapQuery?.year, 2024)
    }

    @Test
    fun `getCategoryTimeline maps category`() = runTest {
        fakeApi.categoryTimelineResult = SpacetimeTimelineDto(
            key = "传统技艺",
            label = "传统技艺",
            buckets = emptyList(),
        )

        val result = repository.getCategoryTimeline("传统技艺")

        assertEquals("传统技艺", result.key)
        assertEquals(fakeApi.capturedCategoryTimelineQuery?.category, "传统技艺")
    }

    @Test
    fun `getAnalyticsFacets maps buckets`() = runTest {
        fakeApi.facetsResult = AnalyticsFacetsDto(
            regions = listOf(AnalyticsFacetBucketDto(key = "浙江", count = 3)),
            categories = listOf(AnalyticsFacetBucketDto(key = "传统技艺", count = 2)),
        )

        val result = repository.getAnalyticsFacets(AnalyticsFilters(region = "浙江"))

        assertEquals(1, result.regions.size)
        assertEquals("浙江", result.regions.first().key)
        assertEquals(fakeApi.capturedFacetsQuery?.filters?.region, "浙江")
    }

    @Test
    fun `getAnalyticsBreakdown maps groupBy`() = runTest {
        fakeApi.breakdownResult = AnalyticsBreakdownDto(
            groupBy = AnalyticsDimension.Region,
            buckets = listOf(AnalyticsBreakdownBucketDto(key = "浙江", total = 3)),
            total = 3,
        )

        val result = repository.getAnalyticsBreakdown(
            groupBy = AnalyticsDimension.Region,
            filters = AnalyticsFilters(),
            limit = 25,
        )

        assertEquals(AnalyticsDimension.Region, result.groupBy)
        assertEquals(1, result.buckets.size)
        assertEquals("浙江", result.buckets.first().key)
        with(fakeApi.capturedBreakdownQuery) {
            assertEquals(AnalyticsDimension.Region, this?.groupBy)
            assertEquals(25, this?.limit)
        }
    }

    @Test
    fun `getAnalyticsCrosstab maps cells`() = runTest {
        fakeApi.crosstabResult = AnalyticsCrosstabDto(
            x = AnalyticsDimension.Region,
            y = AnalyticsDimension.Category,
            cells = emptyList(),
            xBuckets = listOf("浙江"),
            yBuckets = listOf("传统技艺"),
        )

        val result = repository.getAnalyticsCrosstab(
            x = AnalyticsDimension.Region,
            y = AnalyticsDimension.Category,
            filters = AnalyticsFilters(),
        )

        assertEquals(AnalyticsDimension.Region, result.x)
        assertEquals(listOf("浙江"), result.xBuckets)
    }

    @Test
    fun `getAnalyticsCompare maps items and winner`() = runTest {
        fakeApi.compareResult = AnalyticsCompareDto(
            dimension = AnalyticsDimension.Region,
            metric = RankingMetric.Total,
            winnerKey = "浙江",
            items = emptyList(),
        )

        val result = repository.getAnalyticsCompare(
            dimension = AnalyticsDimension.Region,
            keys = listOf("浙江", "江苏"),
            metric = RankingMetric.Total,
            filters = AnalyticsFilters(),
        )

        assertEquals("浙江", result.winnerKey)
        with(fakeApi.capturedCompareQuery) {
            assertEquals(listOf("浙江", "江苏"), this?.keys)
            assertEquals(RankingMetric.Total, this?.metric)
        }
    }

    @Test
    fun `getAnalyticsOutliers maps ratio`() = runTest {
        fakeApi.outliersResult = listOf(
            AnalyticsOutliersDto(
                dimension = AnalyticsDimension.Region,
                key = "浙江",
                metric = RankingMetric.Total,
                value = 10.0,
                average = 5.0,
                ratioToAverage = 2.0,
            ),
        )

        val result = repository.getAnalyticsOutliers(
            dimension = AnalyticsDimension.Region,
            metric = RankingMetric.Total,
            filters = AnalyticsFilters(),
        )

        assertEquals(1, result.size)
        assertEquals(2.0, result.first().ratioToAverage, 0.001)
    }

    @Test
    fun `getRankings maps definitions`() = runTest {
        fakeApi.rankingsResult = listOf(
            RankingDefinitionDto(
                rankingId = "top-regions",
                title = "热门地区",
                metric = "total",
            ),
        )

        val result = repository.getRankings()

        assertEquals(1, result.size)
        assertEquals("top-regions", result.first().rankingId)
        assertEquals("热门地区", result.first().title)
    }

    @Test
    fun `getRankingDetail maps items and metrics`() = runTest {
        fakeApi.rankingDetailResult = RankingDetailDto(
            rankingId = "top-regions",
            title = "热门地区",
            items = listOf(
                RankingItemDto(
                    rank = 1,
                    title = "浙江",
                    score = 95.0,
                    metrics = emptyList(),
                ),
            ),
        )

        val result = repository.getRankingDetail(
            rankingId = "top-regions",
            filters = RankingFilters(region = "浙江", limit = 10),
        )

        assertEquals("top-regions", result.rankingId)
        assertEquals(1, result.items.size)
        assertEquals(1, result.items.first().rank)
        with(fakeApi.capturedRankingDetailQuery) {
            assertEquals("top-regions", this?.rankingId)
            assertEquals("浙江", this?.region)
            assertEquals(10, this?.limit)
        }
    }

    private class FakeDataExploreApi : DataExploreApi {
        var overviewResult: SpacetimeOverviewDto = SpacetimeOverviewDto()
        var heatmapResult: SpacetimeHeatmapDto = SpacetimeHeatmapDto()
        var regionTimelineResult: SpacetimeTimelineDto = SpacetimeTimelineDto(key = "")
        var yearMapResult: SpacetimeRegionMapDto = SpacetimeRegionMapDto(year = 0)
        var categoryTimelineResult: SpacetimeTimelineDto = SpacetimeTimelineDto(key = "")
        var facetsResult: AnalyticsFacetsDto = AnalyticsFacetsDto()
        var breakdownResult: AnalyticsBreakdownDto = AnalyticsBreakdownDto()
        var crosstabResult: AnalyticsCrosstabDto = AnalyticsCrosstabDto()
        var compareResult: AnalyticsCompareDto = AnalyticsCompareDto()
        var outliersResult: List<AnalyticsOutliersDto> = emptyList()
        var rankingsResult: List<RankingDefinitionDto> = emptyList()
        var rankingDetailResult: RankingDetailDto = RankingDetailDto(rankingId = "")

        var capturedOverviewQuery: SpacetimeOverviewQuery? = null
        var capturedHeatmapQuery: SpacetimeHeatmapQuery? = null
        var capturedRegionTimelineQuery: SpacetimeRegionTimelineQuery? = null
        var capturedYearMapQuery: SpacetimeYearMapQuery? = null
        var capturedCategoryTimelineQuery: SpacetimeCategoryTimelineQuery? = null
        var capturedFacetsQuery: AnalyticsFacetsQuery? = null
        var capturedBreakdownQuery: AnalyticsBreakdownQuery? = null
        var capturedCrosstabQuery: AnalyticsCrosstabQuery? = null
        var capturedCompareQuery: AnalyticsCompareQuery? = null
        var capturedOutliersQuery: AnalyticsOutliersQuery? = null
        var capturedRankingDetailQuery: RankingDetailQuery? = null

        override suspend fun getSpacetimeOverview(query: SpacetimeOverviewQuery): SpacetimeOverviewDto {
            capturedOverviewQuery = query
            return overviewResult
        }

        override suspend fun getSpacetimeHeatmap(query: SpacetimeHeatmapQuery): SpacetimeHeatmapDto {
            capturedHeatmapQuery = query
            return heatmapResult
        }

        override suspend fun getSpacetimeRegionTimeline(query: SpacetimeRegionTimelineQuery): SpacetimeTimelineDto {
            capturedRegionTimelineQuery = query
            return regionTimelineResult
        }

        override suspend fun getSpacetimeYearMap(query: SpacetimeYearMapQuery): SpacetimeRegionMapDto {
            capturedYearMapQuery = query
            return yearMapResult
        }

        override suspend fun getSpacetimeCategoryTimeline(query: SpacetimeCategoryTimelineQuery): SpacetimeTimelineDto {
            capturedCategoryTimelineQuery = query
            return categoryTimelineResult
        }

        override suspend fun getAnalyticsFacets(query: AnalyticsFacetsQuery): AnalyticsFacetsDto {
            capturedFacetsQuery = query
            return facetsResult
        }

        override suspend fun getAnalyticsBreakdown(query: AnalyticsBreakdownQuery): AnalyticsBreakdownDto {
            capturedBreakdownQuery = query
            return breakdownResult
        }

        override suspend fun getAnalyticsCrosstab(query: AnalyticsCrosstabQuery): AnalyticsCrosstabDto {
            capturedCrosstabQuery = query
            return crosstabResult
        }

        override suspend fun getAnalyticsCompare(query: AnalyticsCompareQuery): AnalyticsCompareDto {
            capturedCompareQuery = query
            return compareResult
        }

        override suspend fun getAnalyticsOutliers(query: AnalyticsOutliersQuery): List<AnalyticsOutliersDto> {
            capturedOutliersQuery = query
            return outliersResult
        }

        override suspend fun getRankings(): List<RankingDefinitionDto> = rankingsResult

        override suspend fun getRankingDetail(query: RankingDetailQuery): RankingDetailDto {
            capturedRankingDetailQuery = query
            return rankingDetailResult
        }

        override suspend fun getRankingContent(query: com.duckylife.heritage.modern.core.network.RankingContentQuery): RankingDetailDto {
            return rankingDetailResult
        }
    }
}
