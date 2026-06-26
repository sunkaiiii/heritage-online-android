package com.duckylife.heritage.modern.feature.learningroutes


import com.duckylife.heritage.modern.R
import com.duckylife.heritage.modern.core.data.LearningRoutesRepository
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.profile.FakeLocalUserSyncRepository
import com.duckylife.heritage.modern.core.profile.ProfileLearningProgress
import com.duckylife.heritage.modern.core.profile.ProfileSyncStatus
import com.duckylife.heritage.modern.core.testing.MainDispatcherRule
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteDetailUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteNextUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSectionUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteStepUiModel
import com.duckylife.heritage.modern.feature.learningroutes.model.LearningRouteSummaryUiModel
import com.duckylife.heritage.modern.ui.error.ErrorKind
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.ResponseException
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LearningRouteDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val routeId = "route-1"
    private val fakeRepository = FakeLearningRoutesRepository()
    private val fakeSyncRepository = FakeLocalUserSyncRepository()

    @Test
    fun `loads route detail on init`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(routeId, viewModel.uiState.value.route?.routeId)
        assertEquals("Test Route", viewModel.uiState.value.route?.title)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.errorKind)
    }

    @Test
    fun `applies synced progress on collect`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        fakeSyncRepository.emitProgress(
            ProfileLearningProgress(
                id = routeId,
                routeId = routeId,
                routeTitle = "Test Route",
                completedStepIds = listOf("step-1"),
                currentStepId = "step-1",
                percent = 33,
                updatedAt = null,
                completedAt = null,
                syncStatus = ProfileSyncStatus.Synced,
            ),
        )
        advanceUntilIdle()

        assertEquals(setOf("step-1"), viewModel.uiState.value.completedStepIds)
        assertEquals(1, viewModel.uiState.value.completedCount)
    }

    @Test
    fun `onStepChecked updates local state immediately`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)

        assertEquals(setOf("step-1"), viewModel.uiState.value.completedStepIds)
        assertEquals(1, viewModel.uiState.value.completedCount)
    }

    @Test
    fun `debounced progress write sends final state after 600ms`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)
        advanceTimeBy(599)

        assertTrue(fakeSyncRepository.updateProgressCalls.isEmpty())

        advanceTimeBy(2)

        assertEquals(1, fakeSyncRepository.updateProgressCalls.size)
        val call = fakeSyncRepository.updateProgressCalls.first()
        assertEquals(routeId, call.routeId)
        assertEquals(listOf("step-1"), call.completedStepIds)
        assertEquals("step-1", call.currentStepId)
    }

    @Test
    fun `rapid toggles only write once with final state`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)
        advanceTimeBy(200)
        viewModel.onStepChecked("step-2", checked = true)
        advanceTimeBy(200)
        viewModel.onStepChecked("step-1", checked = false)
        advanceUntilIdle()

        assertEquals(1, fakeSyncRepository.updateProgressCalls.size)
        assertEquals(listOf("step-2"), fakeSyncRepository.updateProgressCalls.first().completedStepIds)
    }

    @Test
    fun `snackbar message emitted on first completion`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)
        viewModel.onStepChecked("step-2", checked = true)
        viewModel.onStepChecked("step-3", checked = true)
        advanceUntilIdle()

        val message = viewModel.uiState.value.snackbarMessage
        assertNotNull(message)
        assertEquals(R.string.learning_route_completed_steps_format, message?.resId)
        assertEquals(listOf(3, 3), message?.args)
    }

    @Test
    fun `loadNextStep calls repository and exposes next step`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        fakeRepository.next = LearningRouteNextUiModel(
            routeId = routeId,
            completed = false,
            nextStep = LearningRouteStepUiModel(
                stepId = "step-4",
                order = 4,
                title = "Next Step",
                description = null,
                targetType = null,
                targetId = null,
                reason = null,
                estimatedMinutes = 0,
                required = false,
            ),
            relatedRoutes = emptyList(),
        )
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)
        viewModel.onStepChecked("step-2", checked = true)
        advanceUntilIdle()

        viewModel.loadNextStep()
        advanceUntilIdle()

        assertEquals("step-4", viewModel.uiState.value.nextStep?.stepId)
        assertEquals(listOf("step-1", "step-2"), fakeRepository.capturedNextCompletedIds)
    }

    @Test
    fun `confirmRestart clears local progress and syncs empty list`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()
        viewModel.onStepChecked("step-1", checked = true)
        advanceUntilIdle()
        fakeSyncRepository.updateProgressCalls.clear()

        viewModel.confirmRestart()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.completedStepIds.isEmpty())
        assertEquals(1, fakeSyncRepository.updateProgressCalls.size)
        assertTrue(fakeSyncRepository.updateProgressCalls.first().completedStepIds.isEmpty())
        assertNull(fakeSyncRepository.updateProgressCalls.first().currentStepId)
    }

    @Test
    fun `not found error maps to ErrorKind NotFound`() = runTest {
        fakeRepository.failure = notFoundException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(ErrorKind.NotFound, viewModel.uiState.value.errorKind)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `refresh reloads route detail`() = runTest {
        fakeRepository.failure = serviceUnavailableException()
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(ErrorKind.ServerError, viewModel.uiState.value.errorKind)

        fakeRepository.failure = null
        fakeRepository.detail = buildTestRouteDetail()
        viewModel.refresh()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorKind)
        assertEquals(routeId, viewModel.uiState.value.route?.routeId)
    }

    @Test
    fun `refresh cancels stale load and keeps latest result`() = runTest {
        fakeRepository.detailDelayMs = 300
        fakeRepository.detail = buildTestRouteDetail().copy(
            routeId = "stale-route",
            title = "Stale Route",
        )
        val viewModel = createViewModel()

        viewModel.refresh()
        advanceTimeBy(100)

        fakeRepository.detailDelayMs = 0
        fakeRepository.detail = buildTestRouteDetail()
        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(routeId, viewModel.uiState.value.route?.routeId)
        assertEquals("Test Route", viewModel.uiState.value.route?.title)
    }

    @Test
    fun `pending write prevents synced old progress from reverting local state`() = runTest {
        fakeRepository.detail = buildTestRouteDetail()
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onStepChecked("step-1", checked = true)
        // Emit old progress while debounce is pending; local state should not revert.
        fakeSyncRepository.emitProgress(
            ProfileLearningProgress(
                id = routeId,
                routeId = routeId,
                routeTitle = null,
                completedStepIds = emptyList(),
                currentStepId = null,
                percent = 0,
                updatedAt = null,
                completedAt = null,
                syncStatus = ProfileSyncStatus.Synced,
            ),
        )
        advanceUntilIdle()

        assertEquals(setOf("step-1"), viewModel.uiState.value.completedStepIds)
    }

    private fun createViewModel(): LearningRouteDetailViewModel =
        LearningRouteDetailViewModel(
            routeId = routeId,
            repository = fakeRepository,
            syncRepository = fakeSyncRepository,
        )

    private fun buildTestRouteDetail(): LearningRouteDetailUiModel = LearningRouteDetailUiModel(
        routeId = routeId,
        title = "Test Route",
        description = "Description",
        difficulty = LearningRouteDifficulty.Beginner,
        estimatedMinutes = 30,
        sections = listOf(
            LearningRouteSectionUiModel(
                sectionId = "section-1",
                title = "Section 1",
                description = null,
                stepIds = listOf("step-1", "step-2", "step-3"),
            ),
        ),
        steps = listOf(
            LearningRouteStepUiModel(
                stepId = "step-1",
                order = 1,
                title = "Step 1",
                description = null,
                targetType = null,
                targetId = null,
                reason = null,
                estimatedMinutes = 0,
                required = false,
            ),
            LearningRouteStepUiModel(
                stepId = "step-2",
                order = 2,
                title = "Step 2",
                description = null,
                targetType = null,
                targetId = null,
                reason = null,
                estimatedMinutes = 0,
                required = false,
            ),
            LearningRouteStepUiModel(
                stepId = "step-3",
                order = 3,
                title = "Step 3",
                description = null,
                targetType = null,
                targetId = null,
                reason = null,
                estimatedMinutes = 0,
                required = false,
            ),
        ),
        relatedRoutes = emptyList(),
    )

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

    private class FakeLearningRoutesRepository : LearningRoutesRepository {
        var detail: LearningRouteDetailUiModel? = null
        var next: LearningRouteNextUiModel = LearningRouteNextUiModel(
            routeId = null,
            completed = true,
            nextStep = null,
            relatedRoutes = emptyList(),
        )
        var failure: Throwable? = null
        var detailDelayMs: Long = 1
        var capturedNextCompletedIds: List<String> = emptyList()

        override suspend fun getRoutes(
            difficulty: LearningRouteDifficulty,
            limit: Int,
        ): List<LearningRouteSummaryUiModel> = throw NotImplementedError()

        override suspend fun getRouteDetail(
            routeId: String,
            limit: Int,
            includeAi: Boolean,
        ): LearningRouteDetailUiModel {
            delay(detailDelayMs)
            failure?.let { throw it }
            return requireNotNull(detail)
        }

        override suspend fun buildRoute(
            seedType: LearningRouteSeedType,
            seedKey: String,
            difficulty: LearningRouteDifficulty,
            limit: Int,
        ): LearningRouteDetailUiModel = throw NotImplementedError()

        override suspend fun getNextStep(
            routeId: String,
            completedStepIds: List<String>,
        ): LearningRouteNextUiModel {
            capturedNextCompletedIds = completedStepIds
            failure?.let { throw it }
            return next
        }
    }
}
