package com.duckylife.heritage.modern.core.network

import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import org.junit.Assert.assertEquals
import org.junit.Test

class SpacetimeAnalyticsQueryTest {

    // -----------------------------------------------------------------------
    // Spacetime
    // -----------------------------------------------------------------------

    @Test
    fun `SpacetimeOverviewQuery clamps limit and validates year range`() {
        val query = SpacetimeOverviewQuery(fromYear = 2000, toYear = 2020, limit = 100)
        assertEquals(2000, query.fromYear)
        assertEquals(2020, query.toYear)
        assertEquals(100, query.limit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeOverviewQuery rejects inverted year range`() {
        SpacetimeOverviewQuery(fromYear = 2020, toYear = 2000)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeOverviewQuery rejects limit out of range`() {
        SpacetimeOverviewQuery(limit = 101)
    }

    @Test
    fun `SpacetimeHeatmapQuery validates different dimensions and clamps limit`() {
        val query = SpacetimeHeatmapQuery(
            x = SpacetimeDimension.Region,
            y = SpacetimeDimension.Category,
            fromYear = 2000,
            toYear = 2020,
            limit = 200,
        )
        assertEquals(SpacetimeDimension.Region, query.x)
        assertEquals(SpacetimeDimension.Category, query.y)
        assertEquals(200, query.limit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeHeatmapQuery rejects identical dimensions`() {
        SpacetimeHeatmapQuery(x = SpacetimeDimension.Region, y = SpacetimeDimension.Region)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeHeatmapQuery rejects unknown dimension`() {
        SpacetimeHeatmapQuery(x = SpacetimeDimension.Unknown, y = SpacetimeDimension.Region)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeHeatmapQuery rejects inverted year range`() {
        SpacetimeHeatmapQuery(
            x = SpacetimeDimension.Region,
            y = SpacetimeDimension.Category,
            fromYear = 2020,
            toYear = 2000,
        )
    }

    @Test
    fun `SpacetimeRegionTimelineQuery requires non blank region`() {
        val query = SpacetimeRegionTimelineQuery(region = "浙江")
        assertEquals("浙江", query.region)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeRegionTimelineQuery rejects blank region`() {
        SpacetimeRegionTimelineQuery(region = " ")
    }

    @Test
    fun `SpacetimeYearMapQuery validates year range`() {
        val query = SpacetimeYearMapQuery(year = 1850)
        assertEquals(1850, query.year)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeYearMapQuery rejects invalid year`() {
        SpacetimeYearMapQuery(year = 0)
    }

    @Test
    fun `SpacetimeCategoryTimelineQuery requires non blank category`() {
        val query = SpacetimeCategoryTimelineQuery(category = "传统技艺")
        assertEquals("传统技艺", query.category)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `SpacetimeCategoryTimelineQuery rejects blank category`() {
        SpacetimeCategoryTimelineQuery(category = "")
    }

    // -----------------------------------------------------------------------
    // Analytics
    // -----------------------------------------------------------------------

    @Test
    fun `AnalyticsBreakdownQuery requires known groupBy and clamps limit`() {
        val query = AnalyticsBreakdownQuery(
            groupBy = AnalyticsDimension.Category,
            limit = 100,
            filters = AnalyticsFilters(region = "浙江"),
        )
        assertEquals(AnalyticsDimension.Category, query.groupBy)
        assertEquals(100, query.limit)
        assertEquals("浙江", query.filters.region)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsBreakdownQuery rejects unknown groupBy`() {
        AnalyticsBreakdownQuery(groupBy = AnalyticsDimension.Unknown)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsBreakdownQuery rejects limit out of range`() {
        AnalyticsBreakdownQuery(groupBy = AnalyticsDimension.Region, limit = 101)
    }

    @Test
    fun `AnalyticsCrosstabQuery validates different dimensions and clamps limit`() {
        val query = AnalyticsCrosstabQuery(
            x = AnalyticsDimension.Region,
            y = AnalyticsDimension.Year,
            limit = 200,
            filters = AnalyticsFilters(category = "传统技艺"),
        )
        assertEquals(AnalyticsDimension.Region, query.x)
        assertEquals(AnalyticsDimension.Year, query.y)
        assertEquals(200, query.limit)
        assertEquals("传统技艺", query.filters.category)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsCrosstabQuery rejects identical dimensions`() {
        AnalyticsCrosstabQuery(x = AnalyticsDimension.Region, y = AnalyticsDimension.Region)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsCrosstabQuery rejects unknown dimension`() {
        AnalyticsCrosstabQuery(x = AnalyticsDimension.Unknown, y = AnalyticsDimension.Region)
    }

    @Test
    fun `AnalyticsCompareQuery validates keys count`() {
        val query = AnalyticsCompareQuery(
            dimension = AnalyticsDimension.Region,
            keys = listOf("浙江", "江苏"),
            metric = RankingMetric.Total,
            filters = AnalyticsFilters(),
        )
        assertEquals(listOf("浙江", "江苏"), query.keys)
        assertEquals(RankingMetric.Total, query.metric)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsCompareQuery rejects too many keys`() {
        AnalyticsCompareQuery(
            dimension = AnalyticsDimension.Region,
            keys = List(11) { "key-$it" },
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsCompareQuery rejects unknown dimension`() {
        AnalyticsCompareQuery(
            dimension = AnalyticsDimension.Unknown,
            keys = listOf("浙江"),
        )
    }

    @Test
    fun `AnalyticsOutliersQuery requires known dimension and clamps limit`() {
        val query = AnalyticsOutliersQuery(
            dimension = AnalyticsDimension.Kind,
            metric = RankingMetric.HiddenGem,
            limit = 100,
        )
        assertEquals(AnalyticsDimension.Kind, query.dimension)
        assertEquals(RankingMetric.HiddenGem, query.metric)
        assertEquals(100, query.limit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsOutliersQuery rejects unknown dimension`() {
        AnalyticsOutliersQuery(dimension = AnalyticsDimension.Unknown)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `AnalyticsOutliersQuery rejects limit out of range`() {
        AnalyticsOutliersQuery(dimension = AnalyticsDimension.Region, limit = 101)
    }

    // -----------------------------------------------------------------------
    // Rankings
    // -----------------------------------------------------------------------

    @Test
    fun `RankingDetailQuery clamps limit and accepts filters`() {
        val query = RankingDetailQuery(
            rankingId = "top-regions",
            targetType = "article",
            region = "浙江",
            category = "传统技艺",
            year = 2024,
            limit = 100,
        )
        assertEquals("top-regions", query.rankingId)
        assertEquals("article", query.targetType)
        assertEquals("浙江", query.region)
        assertEquals(2024, query.year)
        assertEquals(100, query.limit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `RankingDetailQuery rejects blank rankingId`() {
        RankingDetailQuery(rankingId = "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun `RankingDetailQuery rejects limit out of range`() {
        RankingDetailQuery(rankingId = "top-regions", limit = 101)
    }

    @Test
    fun `RankingContentQuery requires metric and clamps limit`() {
        val query = RankingContentQuery(
            metric = RankingMetric.Connectivity,
            targetType = "all",
            limit = 50,
        )
        assertEquals(RankingMetric.Connectivity, query.metric)
        assertEquals(50, query.limit)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `RankingContentQuery rejects unknown metric`() {
        RankingContentQuery(metric = RankingMetric.Unknown)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `RankingContentQuery rejects limit out of range`() {
        RankingContentQuery(metric = RankingMetric.Total, limit = 101)
    }
}
