package com.duckylife.heritage.modern.feature.rankings

import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.data.DataExploreRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import com.duckylife.heritage.modern.core.network.dto.advanced.AnalyticsDimension
import com.duckylife.heritage.modern.core.network.dto.advanced.RankingMetric
import com.duckylife.heritage.modern.core.network.dto.advanced.SpacetimeDimension
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.rankings.model.RankingDefinitionUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingDetailUiModel
import com.duckylife.heritage.modern.feature.rankings.model.RankingFilters
import com.duckylife.heritage.modern.feature.rankings.model.RankingItemUiModel
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
class RankingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeDataExploreRepository()

    @Test
    fun `loads rankings on init`() = runTest {
        fakeRepository.rankings = listOf(
            RankingDefinitionUiModel(rankingId = "top-regions", title = "热门地区"),
        )

        val viewModel = RankingsViewModel(repository = fakeRepository)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.definitions.isLoading)
        assertEquals(1, viewModel.uiState.value.definitions.data?.size)
        assertEquals("top-regions", viewModel.uiState.value.definitions.data?.first()?.rankingId)
    }

    @Test
    fun `retry reloads rankings`() = runTest {
        fakeRepository.rankingsFailure = RuntimeException("fail")
        val viewModel = RankingsViewModel(repository = fakeRepository)
        advanceUntilIdle()
        assertEquals(ErrorKind.Unknown, viewModel.uiState.value.definitions.errorKind)

        fakeRepository.rankingsFailure = null
        fakeRepository.rankings = listOf(RankingDefinitionUiModel(rankingId = "r1", title = "R1"))
        viewModel.retry()
        advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.definitions.data?.size)
    }

    @Test
    fun `ranking detail loads on init`() = runTest {
        fakeRepository.rankingDetail = RankingDetailUiModel(
            rankingId = "top-regions",
            title = "热门地区",
            items = listOf(RankingItemUiModel(rank = 1, title = "浙江", score = 90.0)),
        )

        val viewModel = RankingDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = SavedStateHandle(),
        )
        viewModel.setRankingId("top-regions")
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.detail.isLoading)
        assertEquals("top-regions", viewModel.uiState.value.detail.data?.rankingId)
        assertEquals(1, viewModel.uiState.value.detail.data?.items?.size)
    }

    @Test
    fun `ranking detail update filters reloads`() = runTest {
        fakeRepository.rankingDetail = RankingDetailUiModel(rankingId = "top-regions", title = "T")
        val viewModel = RankingDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = SavedStateHandle(),
        )
        viewModel.setRankingId("top-regions")
        advanceUntilIdle()

        viewModel.updateFilters(RankingFilters(region = "浙江", limit = 5))
        advanceUntilIdle()

        assertEquals("浙江", viewModel.uiState.value.filters.region)
        assertEquals(5, viewModel.uiState.value.filters.limit)
        assertEquals("浙江", fakeRepository.lastRankingFilters?.region)
    }

    @Test
    fun `ranking detail saved state restores ranking id`() = runTest {
        val savedStateHandle = SavedStateHandle().apply {
            set("ranking_detail_id", "top-regions")
        }
        fakeRepository.rankingDetail = RankingDetailUiModel(rankingId = "top-regions", title = "T")

        val viewModel = RankingDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = savedStateHandle,
        )
        advanceUntilIdle()

        assertEquals("top-regions", viewModel.uiState.value.rankingId)
        assertEquals("top-regions", viewModel.uiState.value.detail.data?.rankingId)
    }

    @Test
    fun `ranking detail 404 maps to NotFound error`() = runTest {
        fakeRepository.rankingDetailFailure = notFoundException()

        val viewModel = RankingDetailViewModel(
            repository = fakeRepository,
            savedStateHandle = SavedStateHandle(),
        )
        viewModel.setRankingId("missing")
        advanceUntilIdle()

        assertEquals(ErrorKind.NotFound, viewModel.uiState.value.detail.errorKind)
    }

    private suspend fun notFoundException(): ResponseException {
        val client = HttpClient(MockEngine { respond("", status = HttpStatusCode.NotFound) }) {
            expectSuccess = true
        }
        return try {
            client.get("/")
            error("expected exception")
        } catch (e: ResponseException) {
            e
        } finally {
            client.close()
        }
    }

    private class FakeDataExploreRepository : DataExploreRepository {
        var rankings: List<RankingDefinitionUiModel> = emptyList()
        var rankingDetail: RankingDetailUiModel = RankingDetailUiModel(rankingId = "", title = "")
        var rankingContent: RankingDetailUiModel = RankingDetailUiModel(rankingId = "", title = "")
        var rankingsFailure: Throwable? = null
        var rankingDetailFailure: Throwable? = null
        var rankingContentFailure: Throwable? = null
        var lastRankingFilters: RankingFilters? = null
        var lastRankingContentRequest: Pair<RankingMetric, RankingFilters>? = null

        override suspend fun getSpacetimeOverview(filters: SpacetimeFilters): SpacetimeOverviewUiModel =
            throw NotImplementedError()

        override suspend fun getSpacetimeHeatmap(
            x: SpacetimeDimension,
            y: SpacetimeDimension,
            filters: SpacetimeFilters,
        ): SpacetimeHeatmapUiModel = throw NotImplementedError()

        override suspend fun getRegionTimeline(region: String): SpacetimeTimelineUiModel = throw NotImplementedError()
        override suspend fun getYearMap(year: Int): SpacetimeRegionMapUiModel = throw NotImplementedError()
        override suspend fun getCategoryTimeline(category: String): SpacetimeTimelineUiModel = throw NotImplementedError()
        override suspend fun getAnalyticsFacets(filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters): AnalyticsFacetsUiModel =
            throw NotImplementedError()

        override suspend fun getAnalyticsBreakdown(
            groupBy: AnalyticsDimension,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): AnalyticsBreakdownUiModel = throw NotImplementedError()

        override suspend fun getAnalyticsCrosstab(
            x: AnalyticsDimension,
            y: AnalyticsDimension,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): AnalyticsCrosstabUiModel = throw NotImplementedError()

        override suspend fun getAnalyticsCompare(
            dimension: AnalyticsDimension,
            keys: List<String>,
            metric: RankingMetric,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
        ): AnalyticsCompareUiModel = throw NotImplementedError()

        override suspend fun getAnalyticsOutliers(
            dimension: AnalyticsDimension,
            metric: RankingMetric,
            filters: com.duckylife.heritage.modern.core.network.AnalyticsFilters,
            limit: Int,
        ): List<AnalyticsOutlierUiModel> = throw NotImplementedError()

        override suspend fun getRankings(): List<RankingDefinitionUiModel> {
            rankingsFailure?.let { throw it }
            return rankings
        }

        override suspend fun getRankingDetail(
            rankingId: String,
            filters: RankingFilters,
        ): RankingDetailUiModel {
            lastRankingFilters = filters
            rankingDetailFailure?.let { throw it }
            return rankingDetail
        }

        override suspend fun getRankingContent(
            metric: RankingMetric,
            filters: RankingFilters,
        ): RankingDetailUiModel {
            lastRankingContentRequest = metric to filters
            rankingContentFailure?.let { throw it }
            return rankingContent
        }
    }
}
