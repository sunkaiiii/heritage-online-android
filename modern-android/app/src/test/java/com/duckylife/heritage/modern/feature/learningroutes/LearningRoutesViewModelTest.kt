package com.duckylife.heritage.modern.feature.learningroutes

import androidx.lifecycle.SavedStateHandle
import com.duckylife.heritage.modern.core.data.LearningRoutesRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteNextUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LearningRoutesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeLearningRoutesRepository()

    @Test
    fun `loads routes on init with all difficulty`() = runTest {
        fakeRepository.routes = listOf(
            LearningRouteSummaryUiModel(
                routeId = "r1",
                title = "Route 1",
                subtitle = null,
                description = null,
                difficulty = LearningRouteDifficulty.Beginner,
                estimatedMinutes = 10,
                stepCount = 2,
                tags = emptyList(),
                coverImageUrl = null,
            ),
        )

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.routes.isLoading)
        assertEquals(1, state.routes.data?.size)
        assertEquals(LearningRouteDifficulty.All, fakeRepository.capturedDifficulty)
    }

    @Test
    fun `selectDifficulty reloads routes and persists choice`() = runTest {
        fakeRepository.routes = listOf(
            LearningRouteSummaryUiModel(
                routeId = "r2",
                title = "Deep Route",
                subtitle = null,
                description = null,
                difficulty = LearningRouteDifficulty.Deep,
                estimatedMinutes = 60,
                stepCount = 10,
                tags = emptyList(),
                coverImageUrl = null,
            ),
        )
        val savedStateHandle = SavedStateHandle()
        val viewModel = createViewModel(savedStateHandle = savedStateHandle)
        advanceUntilIdle()

        viewModel.selectDifficulty(LearningRouteDifficulty.Deep)
        advanceUntilIdle()

        assertEquals(LearningRouteDifficulty.Deep, viewModel.uiState.value.selectedDifficulty)
        assertEquals(LearningRouteDifficulty.Deep, fakeRepository.capturedDifficulty)
        assertEquals("deep", savedStateHandle.get<String>("learning_routes_selected_difficulty"))
    }

    @Test
    fun `restores difficulty from SavedStateHandle`() = runTest {
        val savedStateHandle = SavedStateHandle().apply {
            set("learning_routes_selected_difficulty", "intermediate")
        }
        fakeRepository.routes = emptyList()

        val viewModel = createViewModel(savedStateHandle = savedStateHandle)
        advanceUntilIdle()

        assertEquals(LearningRouteDifficulty.Intermediate, viewModel.uiState.value.selectedDifficulty)
    }

    @Test
    fun `error state shows retry`() = runTest {
        fakeRepository.failure = serviceUnavailableException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.routes.isLoading)
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.routes.errorKind)
        assertTrue(viewModel.uiState.value.routes.hasFatalError)
    }

    @Test
    fun `retry after failure loads routes`() = runTest {
        fakeRepository.failure = serviceUnavailableException()
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.routes.hasFatalError)

        fakeRepository.failure = null
        fakeRepository.routes = listOf(
            LearningRouteSummaryUiModel(
                routeId = "r1",
                title = "Route",
                subtitle = null,
                description = null,
                difficulty = LearningRouteDifficulty.Beginner,
                estimatedMinutes = 10,
                stepCount = 2,
                tags = emptyList(),
                coverImageUrl = null,
            ),
        )
        viewModel.retry()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.routes.hasFatalError)
        assertEquals(1, viewModel.uiState.value.routes.data?.size)
    }

    @Test
    fun `buildFromSeed sends navigation event with route id`() = runTest {
        fakeRepository.builtDetail = LearningRouteDetailUiModel(
            routeId = "built-1",
            title = "Built Route",
            description = null,
            difficulty = LearningRouteDifficulty.Beginner,
            estimatedMinutes = 20,
            sections = emptyList(),
            steps = emptyList(),
            relatedRoutes = emptyList(),
        )
        val viewModel = createViewModel()
        viewModel.setSeed(seedType = "article", seedId = "a1")
        advanceUntilIdle()

        var navigationEvent: String? = null
        val collectJob = launch {
            viewModel.navigationEvents.collect { navigationEvent = it }
        }

        viewModel.buildFromSeed()
        advanceUntilIdle()
        collectJob.cancel()

        assertEquals(LearningRouteSeedType.Content, fakeRepository.capturedBuildSeedType)
        assertEquals("article:a1", fakeRepository.capturedBuildSeedKey)
        assertEquals("built-1", navigationEvent)
        assertFalse(viewModel.uiState.value.isBuildingSeed)
    }

    @Test
    fun `buildFromSeed with all difficulty falls back to beginner`() = runTest {
        fakeRepository.builtDetail = LearningRouteDetailUiModel(
            routeId = "built-1",
            title = "Built Route",
            description = null,
            difficulty = LearningRouteDifficulty.Beginner,
            estimatedMinutes = 20,
            sections = emptyList(),
            steps = emptyList(),
            relatedRoutes = emptyList(),
        )
        val savedStateHandle = SavedStateHandle()
        val viewModel = createViewModel(savedStateHandle = savedStateHandle)
        viewModel.setSeed(seedType = "article", seedId = "a1")
        viewModel.selectDifficulty(LearningRouteDifficulty.All)
        advanceUntilIdle()

        viewModel.buildFromSeed()
        advanceUntilIdle()

        assertEquals(LearningRouteDifficulty.Beginner, fakeRepository.capturedBuildDifficulty)
    }

    @Test
    fun `buildFromSeed without seed does nothing`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.buildFromSeed()
        advanceUntilIdle()

        assertNull(fakeRepository.capturedBuildSeedType)
        assertFalse(viewModel.uiState.value.isBuildingSeed)
    }

    @Test
    fun `rapid difficulty switch keeps last selected data`() = runTest {
        fakeRepository.routesMap = mapOf(
            LearningRouteDifficulty.All to emptyList(),
            LearningRouteDifficulty.Beginner to listOf(
                LearningRouteSummaryUiModel(
                    routeId = "beginner-route",
                    title = "Beginner",
                    subtitle = null,
                    description = null,
                    difficulty = LearningRouteDifficulty.Beginner,
                    estimatedMinutes = 10,
                    stepCount = 2,
                    tags = emptyList(),
                    coverImageUrl = null,
                ),
            ),
            LearningRouteDifficulty.Intermediate to listOf(
                LearningRouteSummaryUiModel(
                    routeId = "intermediate-route",
                    title = "Intermediate",
                    subtitle = null,
                    description = null,
                    difficulty = LearningRouteDifficulty.Intermediate,
                    estimatedMinutes = 30,
                    stepCount = 5,
                    tags = emptyList(),
                    coverImageUrl = null,
                ),
            ),
        )
        fakeRepository.routeResponseDelay = { difficulty ->
            if (difficulty == LearningRouteDifficulty.Beginner) 200L else 0L
        }

        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.selectDifficulty(LearningRouteDifficulty.Beginner)
        viewModel.selectDifficulty(LearningRouteDifficulty.Intermediate)
        advanceUntilIdle()

        assertEquals(LearningRouteDifficulty.Intermediate, viewModel.uiState.value.selectedDifficulty)
        assertEquals("intermediate-route", viewModel.uiState.value.routes.data?.first()?.routeId)
    }

    private fun createViewModel(
        savedStateHandle: SavedStateHandle = SavedStateHandle(),
    ): LearningRoutesViewModel =
        LearningRoutesViewModel(repository = fakeRepository, savedStateHandle = savedStateHandle)

    private suspend fun serviceUnavailableException(): ResponseException {
        val client = HttpClient(MockEngine { respond("", status = HttpStatusCode.ServiceUnavailable) }) {
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

    private class FakeLearningRoutesRepository : LearningRoutesRepository {
        var routes: List<LearningRouteSummaryUiModel> = emptyList()
        var routesMap: Map<LearningRouteDifficulty, List<LearningRouteSummaryUiModel>>? = null
        var routeResponseDelay: (LearningRouteDifficulty) -> Long = { 0L }
        var builtDetail: LearningRouteDetailUiModel? = null
        var failure: Throwable? = null
        var capturedDifficulty: LearningRouteDifficulty? = null
        var capturedBuildSeedType: LearningRouteSeedType? = null
        var capturedBuildSeedKey: String? = null
        var capturedBuildDifficulty: LearningRouteDifficulty? = null

        override suspend fun getRoutes(
            difficulty: LearningRouteDifficulty,
            limit: Int,
        ): List<LearningRouteSummaryUiModel> {
            capturedDifficulty = difficulty
            val delayMillis = routeResponseDelay(difficulty)
            if (delayMillis > 0) kotlinx.coroutines.delay(delayMillis)
            failure?.let { throw it }
            return routesMap?.get(difficulty) ?: routes
        }

        override suspend fun getRouteDetail(
            routeId: String,
            limit: Int,
            includeAi: Boolean,
        ): LearningRouteDetailUiModel = throw NotImplementedError()

        override suspend fun buildRoute(
            seedType: LearningRouteSeedType,
            seedKey: String,
            difficulty: LearningRouteDifficulty,
            limit: Int,
        ): LearningRouteDetailUiModel {
            capturedBuildSeedType = seedType
            capturedBuildSeedKey = seedKey
            capturedBuildDifficulty = difficulty
            failure?.let { throw it }
            return requireNotNull(builtDetail)
        }

        override suspend fun getNextStep(
            routeId: String,
            completedStepIds: List<String>,
        ): LearningRouteNextUiModel = throw NotImplementedError()
    }
}
