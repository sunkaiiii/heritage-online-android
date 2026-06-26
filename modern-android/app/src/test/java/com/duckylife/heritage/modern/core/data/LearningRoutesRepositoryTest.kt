package com.duckylife.heritage.modern.core.data

import com.duckylife.heritage.modern.core.network.LearningRouteBuildQuery
import com.duckylife.heritage.modern.core.network.LearningRouteDetailQuery
import com.duckylife.heritage.modern.core.network.LearningRouteNextQuery
import com.duckylife.heritage.modern.core.network.LearningRoutesListQuery
import com.duckylife.heritage.modern.core.network.api.LearningRoutesApi
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDetailDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteDifficulty
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteNextDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSeedType
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSectionDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteStepDto
import com.duckylife.heritage.modern.core.network.dto.advanced.LearningRouteSummaryDto
import com.duckylife.heritage.modern.core.profile.LocalProfileRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LearningRoutesRepositoryTest {

    private val fakeApi = FakeLearningRoutesApi()
    private val fakeProfileRepository = FakeLocalProfileRepository()
    private val repository: LearningRoutesRepository = DefaultLearningRoutesRepository(
        api = fakeApi,
        profileRepository = fakeProfileRepository,
    )

    @Test
    fun `getRoutes maps summaries and applies difficulty query`() = runTest {
        fakeApi.routesResult = listOf(
            LearningRouteSummaryDto(
                routeId = "r1",
                title = "入门路线",
                difficulty = LearningRouteDifficulty.Beginner,
                estimatedMinutes = 15,
                stepCount = 3,
                tags = listOf("传统技艺"),
            ),
        )

        val result = repository.getRoutes(difficulty = LearningRouteDifficulty.Beginner, limit = 25)

        assertEquals(1, result.size)
        val route = result.first()
        assertEquals("r1", route.routeId)
        assertEquals("入门路线", route.title)
        assertEquals(LearningRouteDifficulty.Beginner, route.difficulty)
        assertEquals(3, route.stepCount)
        with(fakeApi.capturedListQuery) {
            assertEquals(LearningRouteDifficulty.Beginner, this?.difficulty)
            assertEquals(25, this?.limit)
        }
    }

    @Test
    fun `getRouteDetail maps sections and steps`() = runTest {
        fakeApi.detailResult = LearningRouteDetailDto(
            routeId = "r1",
            title = "路线",
            difficulty = LearningRouteDifficulty.Intermediate,
            estimatedMinutes = 30,
            sections = listOf(
                LearningRouteSectionDto(sectionId = "s1", title = "第一节", stepIds = listOf("step1")),
            ),
            steps = listOf(
                LearningRouteStepDto(stepId = "step1", order = 1, title = "步骤", required = true),
            ),
        )

        val result = repository.getRouteDetail(routeId = "r1", limit = 10, includeAi = true)

        assertEquals("r1", result.routeId)
        assertEquals(1, result.sections.size)
        assertEquals("第一节", result.sections.first().title)
        assertEquals(1, result.steps.size)
        assertTrue(result.steps.first().required)
        with(fakeApi.capturedDetailQuery) {
            assertEquals("r1", this?.routeId)
            assertEquals(10, this?.limit)
            assertEquals(true, this?.includeAi)
        }
    }

    @Test
    fun `buildRoute uses seedKey and clamps difficulty to non-all`() = runTest {
        fakeApi.buildResult = LearningRouteDetailDto(
            routeId = "built-1",
            title = "定制路线",
            difficulty = LearningRouteDifficulty.Beginner,
            estimatedMinutes = 20,
            sections = emptyList(),
            steps = emptyList(),
        )

        val result = repository.buildRoute(
            seedType = LearningRouteSeedType.Content,
            seedKey = "article:a1",
            difficulty = LearningRouteDifficulty.Deep,
            limit = 12,
        )

        assertEquals("built-1", result.routeId)
        with(fakeApi.capturedBuildQuery) {
            assertEquals(LearningRouteSeedType.Content, this?.seedType)
            assertEquals("article:a1", this?.seedKey)
            assertEquals(LearningRouteDifficulty.Deep, this?.difficulty)
            assertEquals(12, this?.limit)
        }
    }

    @Test
    fun `getNextStep passes profileId and completedStepIds`() = runTest {
        fakeProfileRepository.currentProfileIdValue = "android_test_profile"
        fakeApi.nextResult = LearningRouteNextDto(
            routeId = "r1",
            completed = false,
            nextStep = LearningRouteStepDto(stepId = "step2", order = 2, title = "下一步"),
        )

        val result = repository.getNextStep(routeId = "r1", completedStepIds = listOf("step1"))

        assertEquals("r1", result.routeId)
        assertFalse(result.completed)
        assertEquals("step2", result.nextStep?.stepId)
        with(fakeApi.capturedNextQuery) {
            assertEquals("r1", this?.routeId)
            assertEquals(listOf("step1"), this?.completedStepIds)
            assertEquals("android_test_profile", this?.profileId)
        }
    }

    @Test
    fun `getNextStep with empty completedStepIds still passes profileId`() = runTest {
        fakeApi.nextResult = LearningRouteNextDto(routeId = "r1", completed = true)

        val result = repository.getNextStep(routeId = "r1")

        assertTrue(result.completed)
        assertEquals("android_default_profile", fakeApi.capturedNextQuery?.profileId)
        assertTrue(fakeApi.capturedNextQuery?.completedStepIds.isNullOrEmpty())
    }

    private class FakeLearningRoutesApi : LearningRoutesApi {
        var routesResult: List<LearningRouteSummaryDto> = emptyList()
        var detailResult: LearningRouteDetailDto = LearningRouteDetailDto(routeId = "")
        var buildResult: LearningRouteDetailDto = LearningRouteDetailDto(routeId = "")
        var nextResult: LearningRouteNextDto = LearningRouteNextDto()
        var capturedListQuery: LearningRoutesListQuery? = null
        var capturedDetailQuery: LearningRouteDetailQuery? = null
        var capturedBuildQuery: LearningRouteBuildQuery? = null
        var capturedNextQuery: LearningRouteNextQuery? = null

        override suspend fun getLearningRoutes(query: LearningRoutesListQuery): List<LearningRouteSummaryDto> {
            capturedListQuery = query
            return routesResult
        }

        override suspend fun getLearningRouteDetail(query: LearningRouteDetailQuery): LearningRouteDetailDto {
            capturedDetailQuery = query
            return detailResult
        }

        override suspend fun buildLearningRoute(query: LearningRouteBuildQuery): LearningRouteDetailDto {
            capturedBuildQuery = query
            return buildResult
        }

        override suspend fun getLearningRouteNextStep(query: LearningRouteNextQuery): LearningRouteNextDto {
            capturedNextQuery = query
            return nextResult
        }
    }

    private class FakeLocalProfileRepository : LocalProfileRepository {
        var currentProfileIdValue: String = "android_default_profile"

        override val profileId: kotlinx.coroutines.flow.Flow<String>
            get() = kotlinx.coroutines.flow.flowOf(currentProfileIdValue)

        override suspend fun currentProfileId(): String = currentProfileIdValue
    }
}
