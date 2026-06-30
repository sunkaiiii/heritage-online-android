package com.duckylife.heritage.modern.feature.spacetime

import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.data.DataExploreRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsOutliersDto
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsBreakdownUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCompareUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsCrosstabUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsFacetsUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.AnalyticsOutlierUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.NamedCountUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeFilters
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeHeatmapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeOverviewUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeRegionMapUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.SpacetimeTimelineUiModel
import com.duckylife.heritage.modern.feature.spacetime.model.YearCountUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SpacetimeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeDataExploreRepository()

    @Test
    fun `loads overview and heatmap on init`() = runTest {
        fakeRepository.overview = SpacetimeOverviewUiModel(total = 5)
        fakeRepository.heatmap = SpacetimeHeatmapUiModel(
            x = SpacetimeDimension.Region,
            y = SpacetimeDimension.Category,
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.overview.isLoading)
        assertEquals(5, viewModel.uiState.value.overview.data?.total)
        assertFalse(viewModel.uiState.value.heatmap.isLoading)
        assertEquals(SpacetimeDimension.Region, viewModel.uiState.value.heatmap.data?.x)
    }

    @Test
    fun `update filters reloads overview and heatmap`() = runTest {
        fakeRepository.overview = SpacetimeOverviewUiModel(total = 1)
        val viewModel = createViewModel()
        advanceUntilIdle()

        fakeRepository.overview = SpacetimeOverviewUiModel(total = 10)
        viewModel.updateFilters(SpacetimeFilters(region = "浙江"))
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.filters.region)
        assertEquals(10, viewModel.uiState.value.overview.data?.total)
    }

    @Test
    fun `select analytics tab loads breakdown and outliers`() = runTest {
        fakeRepository.breakdown = AnalyticsBreakdownUiModel(
            groupBy = AnalyticsDimension.Region,
            buckets = listOf(AnalyticsBreakdownUiModel.Bucket(key = "浙江", total = 3)),
        )
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Region,
                key = "浙江",
                metric = RankingMetric.Total,
                value = 10.0,
                average = 5.0,
                ratioToAverage = 2.0,
            ),
        )

        val viewModel = createViewModel()
        viewModel.selectTab(1)
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.selectedTab)
        assertEquals("浙江", viewModel.uiState.value.breakdown.data?.buckets?.first()?.key)
        assertEquals(1, viewModel.uiState.value.outliers.data?.size)
    }

    @Test
    fun `set breakdown dimension reloads breakdown`() = runTest {
        fakeRepository.breakdown = AnalyticsBreakdownUiModel(groupBy = AnalyticsDimension.Category)

        val viewModel = createViewModel()
        viewModel.selectTab(1)
        advanceUntilIdle()

        fakeRepository.breakdown = AnalyticsBreakdownUiModel(groupBy = AnalyticsDimension.Region)
        viewModel.setBreakdownDimension(AnalyticsDimension.Region)
        advanceUntilIdle()

        assertEquals(AnalyticsDimension.Region, viewModel.uiState.value.breakdownDimension)
        assertEquals(AnalyticsDimension.Region, viewModel.uiState.value.breakdown.data?.groupBy)
    }

    @Test
    fun `update filters while analytics tab is selected reloads breakdown and outliers`() = runTest {
        fakeRepository.breakdown = AnalyticsBreakdownUiModel(
            groupBy = AnalyticsDimension.Region,
            buckets = listOf(AnalyticsBreakdownUiModel.Bucket(key = "旧筛选", total = 1)),
        )
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Region,
                key = "旧筛选",
                metric = RankingMetric.Total,
                value = 1.0,
                average = 1.0,
                ratioToAverage = 1.0,
            ),
        )
        val viewModel = createViewModel()
        viewModel.selectTab(1)
        advanceUntilIdle()

        fakeRepository.breakdown = AnalyticsBreakdownUiModel(
            groupBy = AnalyticsDimension.Region,
            buckets = listOf(AnalyticsBreakdownUiModel.Bucket(key = "浙江", total = 10)),
        )
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Region,
                key = "浙江",
                metric = RankingMetric.Total,
                value = 10.0,
                average = 2.0,
                ratioToAverage = 5.0,
            ),
        )
        viewModel.updateFilters(SpacetimeFilters(region = "浙江"))
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.breakdown.data?.buckets?.single()?.key)
        assertEquals("浙江", viewModel.uiState.value.outliers.data?.single()?.key)
        assertEquals("浙江", fakeRepository.lastBreakdownFilters?.region)
        assertEquals("浙江", fakeRepository.lastOutlierFilters?.region)
    }

    @Test
    fun `set breakdown dimension also reloads outliers with matching dimension`() = runTest {
        fakeRepository.breakdown = AnalyticsBreakdownUiModel(groupBy = AnalyticsDimension.Region)
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Region,
                key = "浙江",
                metric = RankingMetric.Total,
                value = 10.0,
                average = 5.0,
                ratioToAverage = 2.0,
            ),
        )
        val viewModel = createViewModel()
        viewModel.selectTab(1)
        advanceUntilIdle()

        fakeRepository.breakdown = AnalyticsBreakdownUiModel(groupBy = AnalyticsDimension.Category)
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Category,
                key = "传统技艺",
                metric = RankingMetric.Total,
                value = 7.0,
                average = 2.0,
                ratioToAverage = 3.5,
            ),
        )
        viewModel.setBreakdownDimension(AnalyticsDimension.Category)
        advanceUntilIdle()

        assertEquals(AnalyticsDimension.Category, viewModel.uiState.value.breakdown.data?.groupBy)
        assertEquals(AnalyticsDimension.Category, viewModel.uiState.value.outliers.data?.single()?.dimension)
        assertEquals(AnalyticsDimension.Category, fakeRepository.lastOutlierDimension)
    }

    @Test
    fun `run compare with selected keys`() = runTest {
        fakeRepository.compare = AnalyticsCompareUiModel(
            dimension = AnalyticsDimension.Region,
            winnerKey = "浙江",
        )

        val viewModel = createViewModel()
        viewModel.selectTab(1)
        viewModel.toggleCompareKey("浙江")
        viewModel.toggleCompareKey("江苏")
        viewModel.runCompare()
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.compareResult.data?.winnerKey)
        assertEquals(listOf("浙江", "江苏"), fakeRepository.lastCompareKeys)
    }

    @Test
    fun `run crosstab with selected dimensions`() = runTest {
        fakeRepository.crosstab = AnalyticsCrosstabUiModel(
            x = AnalyticsDimension.Region,
            y = AnalyticsDimension.Year,
        )

        val viewModel = createViewModel()
        viewModel.selectTab(1)
        viewModel.setCrosstabDimensions(AnalyticsDimension.Region, AnalyticsDimension.Year)
        viewModel.runCrosstab()
        advanceUntilIdle()

        assertEquals(AnalyticsDimension.Region, viewModel.uiState.value.crosstabResult.data?.x)
        assertEquals(AnalyticsDimension.Year, viewModel.uiState.value.crosstabResult.data?.y)
    }

    @Test
    fun `load drilldown for region timeline`() = runTest {
        fakeRepository.regionTimeline = SpacetimeTimelineUiModel(key = "浙江")

        val viewModel = createViewModel()
        viewModel.loadDrilldown(DrilldownState.RegionTimeline("浙江"))
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.drilldown.data?.key)
    }

    @Test
    fun `load drilldown for year map`() = runTest {
        fakeRepository.yearMap = SpacetimeRegionMapUiModel(year = 2024)

        val viewModel = createViewModel()
        viewModel.loadDrilldown(DrilldownState.YearMap(2024))
        advanceUntilIdle()

        assertEquals(2024, viewModel.uiState.value.regionMap.data?.year)
    }

    @Test
    fun `error state is mapped`() = runTest {
        fakeRepository.overviewFailure = RuntimeException("network error")

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ErrorKind.Unknown, viewModel.uiState.value.overview.errorKind)
        assertNull(viewModel.uiState.value.overview.data)
    }

    @Test
    fun `saved state restores selected tab and filters`() = runTest {
        val savedStateHandle = SavedStateHandle().apply {
            set("spacetime_selected_tab", 1)
            set("spacetime_filters", SpacetimeFilters(region = "浙江"))
        }
        val viewModel = createViewModel(savedStateHandle)

        assertEquals(1, viewModel.uiState.value.selectedTab)
        assertEquals("浙江", viewModel.uiState.value.filters.region)
    }

    @Test
    fun `restored analytics tab loads visible analytics sections on init`() = runTest {
        fakeRepository.breakdown = AnalyticsBreakdownUiModel(
            groupBy = AnalyticsDimension.Region,
            buckets = listOf(AnalyticsBreakdownUiModel.Bucket(key = "浙江", total = 3)),
        )
        fakeRepository.outliers = listOf(
            AnalyticsOutlierUiModel(
                dimension = AnalyticsDimension.Region,
                key = "浙江",
                metric = RankingMetric.Total,
                value = 10.0,
                average = 5.0,
                ratioToAverage = 2.0,
            ),
        )
        val savedStateHandle = SavedStateHandle().apply {
            set("spacetime_selected_tab", 1)
        }

        val viewModel = createViewModel(savedStateHandle)
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.breakdown.data?.buckets?.single()?.key)
        assertEquals("浙江", viewModel.uiState.value.outliers.data?.single()?.key)
    }

    private fun createViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        SpacetimeViewModel(repository = fakeRepository, savedStateHandle = savedStateHandle)

    private class FakeDataExploreRepository : DataExploreRepository {
        var overview: SpacetimeOverviewUiModel = SpacetimeOverviewUiModel()
        var heatmap: SpacetimeHeatmapUiModel = SpacetimeHeatmapUiModel()
        var regionTimeline: SpacetimeTimelineUiModel = SpacetimeTimelineUiModel(key = "")
        var yearMap: SpacetimeRegionMapUiModel = SpacetimeRegionMapUiModel(year = 0)
        var categoryTimeline: SpacetimeTimelineUiModel = SpacetimeTimelineUiModel(key = "")
        var facets: AnalyticsFacetsUiModel = AnalyticsFacetsUiModel()
        var breakdown: AnalyticsBreakdownUiModel = AnalyticsBreakdownUiModel()
        var crosstab: AnalyticsCrosstabUiModel = AnalyticsCrosstabUiModel()
        var compare: AnalyticsCompareUiModel = AnalyticsCompareUiModel()
        var outliers: List<AnalyticsOutlierUiModel> = emptyList()
        var rankings: List<com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel> = emptyList()
        var rankingDetail: com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel =
            com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel(rankingId = "", title = "")

        var overviewFailure: Throwable? = null
        var heatmapFailure: Throwable? = null
        var lastCompareKeys: List<String> = emptyList()
        var lastBreakdownFilters: com.duckylife.heritage.modern.core.network.AnalyticsFilters? = null
        var lastOutlierFilters: com.duckylife.heritage.modern.core.network.AnalyticsFilters? = null
        var lastOutlierDimension: AnalyticsDimension? = null

        override suspend fun getSpacetimeOverview(filters: SpacetimeFilters): SpacetimeOverviewUiModel {
            overviewFailure?.let { throw it }
            return overview
        }

        override suspend fun getSpacetimeHeatmap(
            x: SpacetimeDimension,
            y: SpacetimeDimension,
            filters: SpacetimeFilters,
        ): SpacetimeHeatmapUiModel {
            heatmapFailure?.let { throw it }
            return heatmap
        }

        override suspend fun getRegionTimeline(region: String): SpacetimeTimelineUiModel = regionTimeline
        override suspend fun getYearMap(year: Int): SpacetimeRegionMapUiModel = yearMap
        override suspend fun getCategoryTimeline(category: String): SpacetimeTimelineUiModel = categoryTimeline
        override suspend fun getAnalyticsFacets(filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters): AnalyticsFacetsUiModel = facets

        override suspend fun getAnalyticsBreakdown(
            groupBy: AnalyticsDimension,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): AnalyticsBreakdownUiModel {
            lastBreakdownFilters = filters
            return breakdown
        }

        override suspend fun getAnalyticsCrosstab(
            x: AnalyticsDimension,
            y: AnalyticsDimension,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): AnalyticsCrosstabUiModel = crosstab

        override suspend fun getAnalyticsCompare(
            dimension: AnalyticsDimension,
            keys: List<String>,
            metric: RankingMetric,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
        ): AnalyticsCompareUiModel {
            lastCompareKeys = keys
            return compare
        }

        override suspend fun getAnalyticsOutliers(
            dimension: AnalyticsDimension,
            metric: RankingMetric,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): List<AnalyticsOutlierUiModel> {
            lastOutlierDimension = dimension
            lastOutlierFilters = filters
            return outliers
        }

        override suspend fun getRankings(): List<com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel> = rankings

        override suspend fun getRankingDetail(
            rankingId: String,
            filters: com.duckylife.heritage.modern.feature.rankings.model.RankingFilters,
        ): com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel = rankingDetail

        override suspend fun getRankingContent(
            metric: RankingMetric,
            filters: com.duckylife.heritage.modern.feature.rankings.model.RankingFilters,
        ): com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel = rankingDetail
    }
}
